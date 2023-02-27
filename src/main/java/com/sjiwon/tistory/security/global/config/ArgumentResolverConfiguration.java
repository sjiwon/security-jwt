package com.sjiwon.tistory.security.global.config;

import com.sjiwon.tistory.security.global.annotation.ExtractPayloadIdArgumentResolver;
import com.sjiwon.tistory.security.global.annotation.ExtractTokenArgumentResolver;
import com.sjiwon.tistory.security.token.utils.JwtTokenProvider;
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
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ExtractTokenArgumentResolver());
        resolvers.add(new ExtractPayloadIdArgumentResolver(jwtTokenProvider));
    }
}
