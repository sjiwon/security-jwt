package com.sjiwon.tistory.security.global.security.service;

import com.sjiwon.tistory.security.global.security.principal.UserAuthenticationDto;
import com.sjiwon.tistory.security.global.security.principal.UserPrincipal;
import com.sjiwon.tistory.security.user.domain.User;
import com.sjiwon.tistory.security.user.domain.UserRepository;
import com.sjiwon.tistory.security.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginIdWithRoles(loginId)
                .orElseThrow(() -> new UsernameNotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage()));

        UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(user, user.getRoles());
        return new UserPrincipal(userAuthenticationDto);
    }
}
