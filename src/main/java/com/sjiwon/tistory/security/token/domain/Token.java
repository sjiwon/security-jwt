package com.sjiwon.tistory.security.token.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    private Long userId;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Builder
    private Token(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public static Token issueToken(Long memberId, String refreshToken) {
        return new Token(memberId, refreshToken);
    }
}
