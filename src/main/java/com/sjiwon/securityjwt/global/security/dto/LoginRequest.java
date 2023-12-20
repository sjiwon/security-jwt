package com.sjiwon.securityjwt.global.security.dto;

public record LoginRequest(
        String loginId,
        String loginPassword
) {
}
