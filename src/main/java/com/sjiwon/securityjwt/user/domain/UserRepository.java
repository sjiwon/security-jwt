package com.sjiwon.securityjwt.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u" +
            " FROM User u" +
            " JOIN FETCH u.roles" +
            " WHERE u.loginId = :loginId")
    Optional<User> findByLoginIdWithRoles(@Param("loginId") String loginId);

    @Query("SELECT u" +
            " FROM User u" +
            " JOIN FETCH u.roles" +
            " WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);
}
