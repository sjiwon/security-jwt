package com.sjiwon.tistory.security.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.tistory.security.global.security.filter.JwtAuthorizationFilter;
import com.sjiwon.tistory.security.global.security.filter.LogoutExceptionTranslationFilter;
import com.sjiwon.tistory.security.global.security.filter.TokenInvalidExceptionTranslationFilter;
import com.sjiwon.tistory.security.global.security.handler.CustomAccessDeniedHandler;
import com.sjiwon.tistory.security.global.security.handler.CustomAuthenticationFailureHandler;
import com.sjiwon.tistory.security.global.security.handler.CustomAuthenticationSuccessHandler;
import com.sjiwon.tistory.security.global.security.handler.CustomLogoutSuccessHandler;
import com.sjiwon.tistory.security.global.security.provider.CustomAuthenticationProvider;
import com.sjiwon.tistory.security.global.security.service.CustomUserDetailsService;
import com.sjiwon.tistory.security.token.service.TokenPersistenceService;
import com.sjiwon.tistory.security.token.utils.JwtTokenProvider;
import com.sjiwon.tistory.security.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final TokenPersistenceService tokenPersistenceService;
    private final UserRepository userRepository;

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService(), passwordEncoder());
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        ProviderManager authenticationManager = (ProviderManager) authenticationConfiguration.getAuthenticationManager();
        authenticationManager.getProviders().add(authenticationProvider());
        return authenticationManager;
    }

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(jwtTokenProvider, tokenPersistenceService, objectMapper);
    }

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(objectMapper);
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper);
    }

    @Bean
    JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtTokenProvider, userRepository);
    }

    @Bean
    TokenInvalidExceptionTranslationFilter tokenInvalidExceptionTranslationFilter() {
        return new TokenInvalidExceptionTranslationFilter(accessDeniedHandler());
    }

    @Bean
    LogoutSuccessHandler jwtLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler(jwtTokenProvider, tokenPersistenceService);
    }

    @Bean
    LogoutExceptionTranslationFilter logoutExceptionTranslationFilter() {
        return new LogoutExceptionTranslationFilter(accessDeniedHandler());
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.httpBasic().disable();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(logoutExceptionTranslationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), LogoutExceptionTranslationFilter.class);
        http.addFilterBefore(tokenInvalidExceptionTranslationFilter(), JwtAuthorizationFilter.class);

        http.formLogin()
                .loginProcessingUrl("/api/login")
                .usernameParameter("loginId")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler());

        http.logout()
                .logoutUrl("/api/logout")
                .clearAuthentication(true)
                .logoutSuccessHandler(jwtLogoutSuccessHandler());

        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

        return http.build();
    }
}
