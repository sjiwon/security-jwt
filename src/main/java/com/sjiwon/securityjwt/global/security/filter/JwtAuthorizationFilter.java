package com.sjiwon.securityjwt.global.security.filter;

import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.global.security.exception.SecurityJwtAccessDeniedException;
import com.sjiwon.securityjwt.global.security.principal.UserPrincipal;
import com.sjiwon.securityjwt.token.exception.InvalidTokenException;
import com.sjiwon.securityjwt.token.utils.RequestTokenExtractor;
import com.sjiwon.securityjwt.token.utils.TokenProvider;
import com.sjiwon.securityjwt.user.domain.model.User;
import com.sjiwon.securityjwt.user.domain.repository.UserRepository;
import com.sjiwon.securityjwt.user.exception.UserErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

        if (token.isPresent()) {
            try {
                final String accessToken = token.get();
                tokenProvider.validateToken(accessToken);

                final User user = getUserByToken(accessToken);
                applyUserToSecurityContext(user);
            } catch (final InvalidTokenException e) {
                accessDeniedHandler.handle(request, response, SecurityJwtAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN));
            }
        }

        filterChain.doFilter(request, response);
    }

    private User getUserByToken(final String accessToken) {
        return userRepository.findByIdWithRoles(tokenProvider.getId(accessToken))
                .orElseThrow(() -> SecurityJwtAccessDeniedException.type(UserErrorCode.USER_NOT_FOUND));
    }

    private void applyUserToSecurityContext(final User user) {
        final UserPrincipal principal = new UserPrincipal(user);
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
