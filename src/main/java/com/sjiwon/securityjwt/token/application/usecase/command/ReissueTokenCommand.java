package com.sjiwon.securityjwt.token.application.usecase.command;

public record ReissueTokenCommand(
        Long userId,
        String refreshToken
) {
}
