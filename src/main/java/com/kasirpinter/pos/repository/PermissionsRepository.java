package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {

    Optional<Permissions> findByName(String permissionName);

    boolean existsByName(String name);
}