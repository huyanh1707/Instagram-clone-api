package com.ju17th.instagramcloneapi.repository;

import com.ju17th.instagramcloneapi.entity.ERole;
import com.ju17th.instagramcloneapi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
