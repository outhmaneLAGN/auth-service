package com.eqdom.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eqdom.auth.entity.RoleName;
import com.eqdom.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    long countByEnabledTrueAndRoles_Name(RoleName roleName);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByCin(String cin);
}
