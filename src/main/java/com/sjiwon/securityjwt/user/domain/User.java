package com.sjiwon.securityjwt.user.domain;

import com.sjiwon.securityjwt.user.domain.role.Role;
import com.sjiwon.securityjwt.user.domain.role.RoleType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, updatable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final Set<Role> roles = new HashSet<>();

    @Builder
    private User(final String name, final String loginId, final String password) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
    }

    public void applyUserRoles(final Set<RoleType> roleTypes) {
        this.roles.addAll(
                roleTypes.stream()
                        .map(roleType ->  Role.createRole(this, roleType))
                        .collect(Collectors.toSet())
        );
    }
}
