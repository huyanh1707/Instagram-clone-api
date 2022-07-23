package com.ju17th.instagramcloneapi.repository;

import com.ju17th.instagramcloneapi.model.user.Role;
import com.ju17th.instagramcloneapi.model.user.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}