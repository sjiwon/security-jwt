package com.sjiwon.securityjwt.token.presentation;

import com.sjiwon.securityjwt.global.annotation.ExtractToken;
import com.sjiwon.securityjwt.token.application.usecase.ReissueTokenUseCase;
import com.sjiwon.securityjwt.token.application.usecase.command.ReissueTokenCommand;
import com.sjiwon.securityjwt.token.domain.model.AuthToken;
import com.sjiwon.securityjwt.token.domain.model.TokenType;
import com.sjiwon.securityjwt.token.utils.TokenResponseWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final ReissueTokenUseCase reissueTokenUseCase;
    private final TokenResponseWriter tokenResponseWriter;

    @PostMapping
    public ResponseEntity<Void> reissueTokens(
            @ExtractToken(tokenType = TokenType.REFRESH) final String refreshToken,
            final HttpServletResponse response
    ) {
        final AuthToken authToken = reissueTokenUseCase.invoke(new ReissueTokenCommand(refreshToken));
        tokenResponseWriter.applyToken(response, authToken);

        return ResponseEntity.noContent().build();
    }
}
