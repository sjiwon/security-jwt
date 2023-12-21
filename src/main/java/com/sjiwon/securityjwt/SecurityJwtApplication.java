package com.sjiwon.securityjwt;

import com.sjiwon.securityjwt.user.domain.model.RoleType;
import com.sjiwon.securityjwt.user.domain.model.User;
import com.sjiwon.securityjwt.user.domain.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class SecurityJwtApplication {
    public static void main(final String[] args) {
        SpringApplication.run(SecurityJwtApplication.class, args);
    }

//    @Bean
    public ApplicationRunner applicationRunner(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder
    ) {
        return args -> userRepository.save(new User(
                "user",
                passwordEncoder.encode("1234"),
                "user",
                Set.of(RoleType.USER)
        ));
    }
}
