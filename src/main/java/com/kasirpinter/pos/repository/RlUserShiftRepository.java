package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.MsShift;
import com.kasirpinter.pos.entity.RlUserShift;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RlUserShiftRepository extends JpaRepository<RlUserShift, Long> {

    Optional<RlUserShift> findByUserAndDate(Users user, LocalDate localDate);

    boolean existsByShift(MsShift data);

    @Modifying
    @Transactional
    @Query("""
            UPDATE RlUserShift rl
            SET rl.tsIn = CASE WHEN :clockIn = true THEN :now ELSE rl.tsIn END,
            rl.tsOut = CASE WHEN :clockIn = false THEN :now ELSE rl.tsOut END,
            rl.updatedAt = :now
            WHERE rl.id = :id
            """)
    void updateTsByUserShiftId(@Param("id") Long id, @Param("now") LocalDateTime now, @Param("clockIn") boolean clockIn);

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM RlUserShift d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);

    boolean existsByUserAndDate(Users user, LocalDate now);

    Page<RlUserShift> findByUserNameLikeIgnoreCase(String keyword, Pageable pageable);

    RlUserShift findByUserAndShift(Users user, MsShift shift);
}
