package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.Member;
import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM Member d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);

    Optional<Member> findByPhone(String customerName);
}