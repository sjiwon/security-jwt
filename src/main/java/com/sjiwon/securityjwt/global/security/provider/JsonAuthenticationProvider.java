package com.sjiwon.securityjwt.global.security.provider;

import com.sjiwon.securityjwt.user.exception.UserErrorCode;
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
public class JsonAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = (String) authentication.getPrincipal();
        final String password = (String) authentication.getCredentials();

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        validatePassword(password, userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void validatePassword(final String rawPassword, final UserDetails userDetails) {
        if (isNotCorrectPassword(rawPassword, userDetails)) {
            throw new BadCredentialsException(UserErrorCode.INVALID_PASSWORD.getMessage());
        }
    }

    private boolean isNotCorrectPassword(final String rawPassword, final UserDetails userDetails) {
        return userDetails == null || !passwordEncoder.matches(rawPassword, userDetails.getPassword());
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
