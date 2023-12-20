package com.sjiwon.securityjwt.token.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // @Query
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Token t" +
            " SET t.refreshToken = :newRefreshToken" +
            " WHERE t.userId = :userId")
    void reissueRefreshTokenByRtrPolicy(@Param("userId") Long userId, @Param("newRefreshToken") String newRefreshToken);

    // Query Method
    boolean existsByUserId(Long userId);
    boolean existsByUserIdAndRefreshToken(Long userId, String refreshToken);
    void deleteByUserId(Long userId);
}
