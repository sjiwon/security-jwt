package com.sjiwon.tistory.security.global.security.filter;

import com.sjiwon.tistory.security.global.security.exception.AuthErrorCode;
import com.sjiwon.tistory.security.global.security.exception.TistorySecurityAccessDeniedException;
import com.sjiwon.tistory.security.global.security.principal.UserAuthenticationDto;
import com.sjiwon.tistory.security.global.security.principal.UserPrincipal;
import com.sjiwon.tistory.security.token.utils.AuthorizationExtractor;
import com.sjiwon.tistory.security.token.utils.JwtTokenProvider;
import com.sjiwon.tistory.security.user.domain.User;
import com.sjiwon.tistory.security.user.domain.UserRepository;
import com.sjiwon.tistory.security.user.domain.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = AuthorizationExtractor.extractToken(request);

        if (token != null) {
            if (jwtTokenProvider.isTokenValid(token)) {
                Long userId = jwtTokenProvider.getId(token);
                User user = userRepository.findByIdWithRoles(userId)
                        .orElseThrow(() -> TistorySecurityAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN));
                Set<Role> roles = user.getRoles();

                UserPrincipal principal = new UserPrincipal(new UserAuthenticationDto(user, roles));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, "", generateUserRoles(roles));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw TistorySecurityAccessDeniedException.type(AuthErrorCode.EXPIRED_OR_POLLUTED_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Collection<GrantedAuthority> generateUserRoles(Set<Role> role) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        addRoles(authorities, role);
        return authorities;
    }

    private void addRoles(Collection<GrantedAuthority> authorities, Set<Role> roles) {
        roles.stream()
                .map(Role::getAuthority)
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
    }
}
