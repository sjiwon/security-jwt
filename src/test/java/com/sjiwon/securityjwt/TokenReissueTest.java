package com.sjiwon.securityjwt;

import com.sjiwon.securityjwt.common.SecurityIntegrationTest;
import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.token.domain.model.Token;
import com.sjiwon.securityjwt.token.domain.repository.TokenRepository;
import com.sjiwon.securityjwt.token.utils.TokenProvider;
import com.sjiwon.securityjwt.token.utils.TokenResponseWriter;
import com.sjiwon.securityjwt.user.domain.model.RoleType;
import com.sjiwon.securityjwt.user.domain.model.User;
import com.sjiwon.securityjwt.user.domain.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Spring Security 토큰 재발급 테스트")
public class TokenReissueTest extends SecurityIntegrationTest {
    private static final String TOKEN_REISSUE_URL = "/api/token/reissue";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenProvider tokenProvider;

    private User user;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(
                "sjiwon",
                passwordEncoder.encode("helloworld1234"),
                "서지원",
                Set.of(RoleType.USER)
        ));
        refreshToken = tokenProvider.createRefreshToken(user.getId());
        tokenRepository.save(Token.issueRefreshToken(user.getId(), refreshToken));
    }

    @Test
    @DisplayName("1. HTTP Cookie에 RefreshToken이 없으면 토큰 재발급에 실패한다")
    void throwExceptionByRefreshTokenNotExists() throws Exception {
        // when
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(TOKEN_REISSUE_URL);

        // then
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andExpectAll(getResultMatchersViaErrorCode(expectedError));
    }

    @Test
    @DisplayName("2. 토큰 재발급에 성공한다")
    void success() throws Exception {
        // when
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(TOKEN_REISSUE_URL)
                .cookie(new Cookie(TokenResponseWriter.REFRESH_TOKEN_COOKIE, refreshToken));

        // then
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isNoContent(),
                        header().exists(HttpHeaders.AUTHORIZATION), // AccessToken
                        cookie().exists(TokenResponseWriter.REFRESH_TOKEN_COOKIE), // RefreshToken with RTR
                        cookie().value(TokenResponseWriter.REFRESH_TOKEN_COOKIE, notNullValue(String.class))
                );

        assertThat(tokenRepository.findByUserId(user.getId())).isPresent();
    }
}
