package com.sjiwon.securityjwt.user.domain.repository;

import com.sjiwon.securityjwt.user.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
