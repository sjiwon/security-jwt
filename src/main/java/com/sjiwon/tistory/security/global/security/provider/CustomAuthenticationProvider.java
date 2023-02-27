package com.sjiwon.tistory.security.global.security.provider;

import com.sjiwon.tistory.security.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        validatePassword(password, userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void validatePassword(String rawPassword, UserDetails userDetails) {
        if (isNotCorrectPassword(rawPassword, userDetails)) {
            throw new BadCredentialsException(UserErrorCode.INVALID_PASSWORD.getMessage());
        }
    }

    private boolean isNotCorrectPassword(String rawPassword, UserDetails userDetails) {
        return userDetails == null || !passwordEncoder.matches(rawPassword, userDetails.getPassword());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
