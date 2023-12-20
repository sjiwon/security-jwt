package com.sjiwon.securityjwt.global.security.filter;

import com.sjiwon.securityjwt.global.security.exception.AuthErrorCode;
import com.sjiwon.securityjwt.global.security.exception.TistorySecurityAccessDeniedException;
import com.sjiwon.securityjwt.global.security.principal.UserAuthenticationDto;
import com.sjiwon.securityjwt.global.security.principal.UserPrincipal;
import com.sjiwon.securityjwt.token.utils.AuthorizationExtractor;
import com.sjiwon.securityjwt.token.utils.JwtTokenProvider;
import com.sjiwon.securityjwt.user.domain.User;
import com.sjiwon.securityjwt.user.domain.UserRepository;
import com.sjiwon.securityjwt.user.domain.role.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final String token = AuthorizationExtractor.extractToken(request);

        if (token != null) {
            if (jwtTokenProvider.isTokenValid(token)) {
                final Long userId = jwtTokenProvider.getId(token);
                final User user = userRepository.findByIdWithRoles(userId)
                        .orElseThrow(() -> TistorySecurityAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN));
                final Set<Role> roles = user.getRoles();

                final UserPrincipal principal = new UserPrincipal(new UserAuthenticationDto(user, roles));
                final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, "", generateUserRoles(roles));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw TistorySecurityAccessDeniedException.type(AuthErrorCode.EXPIRED_OR_POLLUTED_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Collection<GrantedAuthority> generateUserRoles(final Set<Role> role) {
        final Collection<GrantedAuthority> authorities = new ArrayList<>();
        addRoles(authorities, role);
        return authorities;
    }

    private void addRoles(final Collection<GrantedAuthority> authorities, final Set<Role> roles) {
        roles.stream()
                .map(Role::getAuthority)
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
    }
}
