package com.sjiwon.tistory.security.global;

import com.sjiwon.tistory.security.user.domain.User;
import com.sjiwon.tistory.security.user.domain.UserRepository;
import com.sjiwon.tistory.security.user.domain.role.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DummyUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initUser() {
        User user = User.builder()
                .name("user")
                .loginId("user")
                .password(passwordEncoder.encode("1234"))
                .build();
        user.applyUserRoles(Set.of(RoleType.USER));
        userRepository.save(user);
    }
}
