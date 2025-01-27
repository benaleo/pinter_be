package com.kopibery.pos.repository;

import com.kopibery.pos.entity.Roles;
import com.kopibery.pos.model.projection.CastIdSecureIdProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByName(String name);

    @Query("""
            SELECT new com.kopibery.pos.model.projection.CastIdSecureIdProjection(r.id, r.secureId)
            FROM Roles r
            WHERE r.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);
}