package com.sjiwon.securityjwt.token.controller;

import com.sjiwon.securityjwt.global.annotation.ExtractPayloadId;
import com.sjiwon.securityjwt.global.annotation.ExtractToken;
import com.sjiwon.securityjwt.global.security.TokenResponse;
import com.sjiwon.securityjwt.token.service.TokenReissueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final TokenReissueService tokenReissueService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<TokenResponse> reissueTokens(@ExtractPayloadId final Long userId, @ExtractToken final String refreshToken) {
        final TokenResponse tokenResponse = tokenReissueService.reissueTokens(userId, refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
