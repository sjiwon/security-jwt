package com.sjiwon.tistory.security.user.domain.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "사용자"),
    ;

    private final String authority;
    private final String description;
}
