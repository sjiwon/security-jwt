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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Spring Security 로그아웃 테스트")
public class SecurityLogoutTest extends SecurityIntegrationTest {
    private static final String LOGOUT_URL = "/api/logout";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenProvider tokenProvider;

    private User user;
    private String accessToken;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(
                "sjiwon",
                passwordEncoder.encode("helloworld1234"),
                "서지원",
                Set.of(RoleType.USER)
        ));
        accessToken = tokenProvider.createAccessToken(user.getId());
        tokenRepository.save(Token.issueRefreshToken(user.getId(), tokenProvider.createRefreshToken(user.getId())));
    }

    @Test
    @DisplayName("1. 인증되지 않은(Empty SecurityContext Via AccessToken) 사용자면 로그아웃에 실패한다")
    void throwExceptionByAnonymousUser() throws Exception {
        // when
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOGOUT_URL);

        // then
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andExpectAll(getResultMatchersViaErrorCode(expectedError));
    }

    @Test
    @DisplayName("2. 로그아웃에 성공한다")
    void success() throws Exception {
        // when
        final RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOGOUT_URL)
                .header(HttpHeaders.AUTHORIZATION, String.join(" ", TokenResponseWriter.AUTHORIZATION_HEADER_TOKEN_PREFIX, accessToken));

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        assertThat(tokenRepository.findByUserId(user.getId())).isEmpty();
    }
}
