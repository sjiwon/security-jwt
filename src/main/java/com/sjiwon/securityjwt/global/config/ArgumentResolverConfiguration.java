package com.sjiwon.securityjwt.global.config;

import com.sjiwon.securityjwt.global.annotation.ExtractPayloadIdArgumentResolver;
import com.sjiwon.securityjwt.global.annotation.ExtractTokenArgumentResolver;
import com.sjiwon.securityjwt.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ArgumentResolverConfiguration implements WebMvcConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ExtractTokenArgumentResolver());
        resolvers.add(new ExtractPayloadIdArgumentResolver(jwtTokenProvider));
    }
}
