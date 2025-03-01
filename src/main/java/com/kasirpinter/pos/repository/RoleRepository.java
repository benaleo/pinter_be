package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;
import com.kasirpinter.pos.entity.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<com.kasirpinter.pos.entity.Roles, Long> {

    Optional<com.kasirpinter.pos.entity.Roles> findByName(String name);

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(r.id, r.secureId)
            FROM Roles r
            WHERE r.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);

    Page<com.kasirpinter.pos.entity.Roles> findByNameLikeIgnoreCase(String keyword, Pageable pageable);
}