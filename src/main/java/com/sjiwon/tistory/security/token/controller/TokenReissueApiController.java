package com.sjiwon.tistory.security.token.controller;

import com.sjiwon.tistory.security.global.annotation.ExtractPayloadId;
import com.sjiwon.tistory.security.global.annotation.ExtractToken;
import com.sjiwon.tistory.security.global.security.TokenResponse;
import com.sjiwon.tistory.security.token.service.TokenReissueService;
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
    public ResponseEntity<TokenResponse> reissueTokens(@ExtractPayloadId Long userId, @ExtractToken String refreshToken) {
        TokenResponse tokenResponse = tokenReissueService.reissueTokens(userId, refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
