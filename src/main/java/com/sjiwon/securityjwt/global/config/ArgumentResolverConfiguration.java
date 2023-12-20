package com.sjiwon.securityjwt.global.config;

import com.sjiwon.securityjwt.global.annotation.AuthArgumentResolver;
import com.sjiwon.securityjwt.global.annotation.ExtractTokenArgumentResolver;
import com.sjiwon.securityjwt.token.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ArgumentResolverConfiguration implements WebMvcConfigurer {
    private final TokenProvider tokenProvider;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver(tokenProvider));
        resolvers.add(new ExtractTokenArgumentResolver(tokenProvider));
    }
}
