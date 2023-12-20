package com.sjiwon.securityjwt.global.security.principal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sjiwon.securityjwt.user.domain.User;
import com.sjiwon.securityjwt.user.domain.role.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserAuthenticationDto {
    private Long id;
    private String loginId;
    @JsonIgnore
    private String loginPassword;
    private String name;
    private List<String> roles;

    public UserAuthenticationDto(final User user, final Set<Role> roles) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.loginPassword = user.getPassword();
        this.name = user.getName();
        this.roles = roles.stream()
                .map(Role::getAuthority)
                .toList();
    }
}
