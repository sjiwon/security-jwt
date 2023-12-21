package com.sjiwon.securityjwt;

import com.sjiwon.securityjwt.common.SecurityIntegrationTest;
import com.sjiwon.securityjwt.global.security.dto.LoginRequest;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.token.domain.repository.TokenRepository;
import com.sjiwon.securityjwt.token.utils.TokenResponseWriter;
import com.sjiwon.securityjwt.user.domain.model.RoleType;
import com.sjiwon.securityjwt.user.domain.model.User;
import com.sjiwon.securityjwt.user.domain.repository.UserRepository;
import com.sjiwon.securityjwt.user.exception.UserErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Spring Security 인증(로그인) 테스트")
public class SecurityLoginTest extends SecurityIntegrationTest {
    private static final String LOGIN_URL = "/api/login";
    private static final String LOGIN_ID = "sjiwon";
    private static final String LOGIN_PASSWORD = "helloworld1234";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenRepository tokenRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(
                LOGIN_ID,
                passwordEncoder.encode(LOGIN_PASSWORD),
                "서지원",
                Set.of(RoleType.USER)
        ));
    }

    @Test
    @DisplayName("1. application/json ContentType으로 요청하지 않으면 로그인에 실패한다")
    void throwExceptionByInvalidApiRequestContentType() throws Exception {
        // when
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("loginId", LOGIN_ID)
                .param("loginPassword", LOGIN_PASSWORD);

        // then
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_AUTH_CONTENT_TYPE;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpectAll(getResultMatchersViaErrorCode(expectedError));
    }

    @Test
    @DisplayName("2. 로그인 아이디 or 비밀번호를 빈값으로 보내면 로그인에 실패한다")
    void throwExceptionByInvalidRequestData() throws Exception {
        // when
        final RequestBuilder requestBuilderA = MockMvcRequestBuilders
                .post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toBody(new LoginRequest("", LOGIN_PASSWORD)));
        final RequestBuilder requestBuilderB = MockMvcRequestBuilders
                .post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toBody(new LoginRequest(LOGIN_ID, "")));
        final RequestBuilder requestBuilderC = MockMvcRequestBuilders
                .post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toBody(new LoginRequest("", "")));

        // then
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_AUTH_DATA;
        mockMvc.perform(requestBuilderA)
                .andExpect(status().isBadRequest())
                .andExpectAll(getResultMatchersViaErrorCode(expectedError));
        mockMvc.perform(requestBuilderB)
                .andExpect(status().isBadRequest())
                .andExpectAll(getResultMatchersViaErrorCode(expectedError));
        mockMvc.perform(requestBuilderC)
                .andExpect(status().isBadRequest())
                .andExpectAll(getResultMatchersViaErrorCode(expectedError));
    }

    @Test
    @DisplayName("3. 아이디에 해당하는 사용자가 존재하지 않으면 로그인에 실패한다")
    void throwExceptionByInvalidLoginId() throws Exception {
        // when
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toBody(new LoginRequest(LOGIN_ID + "fake", LOGIN_PASSWORD)));

        // then
        final UserErrorCode expectedError = UserErrorCode.USER_NOT_FOUND;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpectAll(getResultMatchersViaErrorCode(expectedError));
    }

    @Test
    @DisplayName("4. 사용자의 비밀번호가 일치하지 않으면 로그인에 실패한다")
    void throwExceptionByInvalidPassword() throws Exception {
        // when
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toBody(new LoginRequest(LOGIN_ID, LOGIN_PASSWORD + "fake")));

        // then
        final UserErrorCode expectedError = UserErrorCode.INVALID_PASSWORD;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpectAll(getResultMatchersViaErrorCode(expectedError));
    }

    @Test
    @DisplayName("5. 로그인에 성공한다")
    void success() throws Exception {
        // when
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toBody(new LoginRequest(LOGIN_ID, LOGIN_PASSWORD)));

        // then
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        header().exists(HttpHeaders.AUTHORIZATION), // AccessToken
                        cookie().exists(TokenResponseWriter.REFRESH_TOKEN_COOKIE), // RefreshToken
                        cookie().value(TokenResponseWriter.REFRESH_TOKEN_COOKIE, notNullValue(String.class)),
                        jsonPath("$.id", notNullValue(Long.class)),
                        jsonPath("$.name", is(user.getName()))
                );

        assertThat(tokenRepository.findByUserId(user.getId())).isPresent();
    }
}
