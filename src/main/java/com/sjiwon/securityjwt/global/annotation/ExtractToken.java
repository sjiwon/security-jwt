package com.sjiwon.securityjwt.global.annotation;

import com.sjiwon.securityjwt.token.domain.model.TokenType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtractToken {
    TokenType tokenType() default TokenType.ACCESS;
}
