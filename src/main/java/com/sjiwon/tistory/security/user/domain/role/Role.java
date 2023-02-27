package com.sjiwon.tistory.security.user.domain.role;

import com.sjiwon.tistory.security.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Builder
    private Role(User user, RoleType roleType) {
        this.user = user;
        this.roleType = roleType;
    }

    public static Role createRole(User user, RoleType roleType) {
        return new Role(user, roleType);
    }

    public String getAuthority() {
        return roleType.getAuthority();
    }
}
