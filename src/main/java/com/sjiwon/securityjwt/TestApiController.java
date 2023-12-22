package com.sjiwon.securityjwt;

import com.sjiwon.securityjwt.global.annotation.Auth;
import com.sjiwon.securityjwt.global.annotation.ExtractToken;
import com.sjiwon.securityjwt.token.domain.model.Authenticated;
import com.sjiwon.securityjwt.token.domain.model.TokenType;
import com.sjiwon.securityjwt.token.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestApiController {
    private final TokenProvider tokenProvider;

    public record AuthInfo(
            Long id,
            String token
    ) {
    }

    @GetMapping("/call-with-access-token")
    public AuthInfo withAccessToken(
            @ExtractToken(tokenType = TokenType.ACCESS) final String accessToken
    ) {
        return new AuthInfo(tokenProvider.getId(accessToken), accessToken);
    }

    @GetMapping("/call-with-refresh-token")
    public AuthInfo withRefreshToken(
            @ExtractToken(tokenType = TokenType.REFRESH) final String refreshToken
    ) {
        return new AuthInfo(tokenProvider.getId(refreshToken), refreshToken);
    }

    @GetMapping("/get-auth-info")
    public Authenticated withRefreshToken(
            @Auth final Authenticated authenticated
    ) {
        return authenticated;
    }
}
