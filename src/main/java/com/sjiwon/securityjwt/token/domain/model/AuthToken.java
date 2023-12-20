package com.sjiwon.securityjwt.token.domain.model;

public record AuthToken(
        String accessToken,
        String refreshToken
) {
}
