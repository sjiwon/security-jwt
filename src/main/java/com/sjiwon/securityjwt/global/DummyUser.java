package com.sjiwon.securityjwt.global;

import com.sjiwon.securityjwt.user.domain.model.RoleType;
import com.sjiwon.securityjwt.user.domain.model.User;
import com.sjiwon.securityjwt.user.domain.repository.UserRepository;
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
        final User user = new User(
                "user",
                passwordEncoder.encode("1234"),
                "user",
                Set.of(RoleType.USER)
        );
        userRepository.save(user);
    }
}
