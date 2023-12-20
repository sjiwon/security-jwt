package com.sjiwon.securityjwt.token.domain.model;

public record Authenticated(
        Long id,
        String accessToken
) {
}
