package com.sjiwon.securityjwt.global.security.provider;

import com.sjiwon.securityjwt.global.security.principal.UserPrincipal;
import com.sjiwon.securityjwt.user.domain.model.User;
import com.sjiwon.securityjwt.user.domain.repository.UserRepository;
import com.sjiwon.securityjwt.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class RdbUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String loginId) throws UsernameNotFoundException {
        final User user = userRepository.findByLoginIdWithRoles(loginId)
                .orElseThrow(() -> new UsernameNotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage()));
        return new UserPrincipal(user);
    }
}
