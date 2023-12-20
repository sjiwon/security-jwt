package com.sjiwon.securityjwt.global.security.service;

import com.sjiwon.securityjwt.global.security.principal.UserAuthenticationDto;
import com.sjiwon.securityjwt.global.security.principal.UserPrincipal;
import com.sjiwon.securityjwt.user.domain.User;
import com.sjiwon.securityjwt.user.domain.UserRepository;
import com.sjiwon.securityjwt.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String loginId) throws UsernameNotFoundException {
        final User user = userRepository.findByLoginIdWithRoles(loginId)
                .orElseThrow(() -> new UsernameNotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage()));

        final UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(user, user.getRoles());
        return new UserPrincipal(userAuthenticationDto);
    }
}
