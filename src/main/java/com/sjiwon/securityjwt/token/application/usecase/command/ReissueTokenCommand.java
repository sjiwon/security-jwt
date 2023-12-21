package com.sjiwon.securityjwt.token.application.usecase.command;

public record ReissueTokenCommand(
        String refreshToken
) {
}
