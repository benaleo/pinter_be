package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.model.dto.SavedLongAndStringValue;
import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findBySecureId(String id);

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM Users d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);


    @Query("""
            SELECT u FROM Users u
            WHERE
                (LOWER(u.name) LIKE LOWER(:keyword) OR
                LOWER(u.email) LIKE LOWER(:keyword))
            """)
    Page<Users> findDataByKeyword(String keyword, Pageable pageable);

    Optional<Users> findByEmailIgnoreCase(String email);

    List<Users> findBySecureIdIn(List<String> userIds);

    boolean existsByCompany(Company c);

    @Query("""
            SELECT new com.kasirpinter.pos.model.dto.SavedLongAndStringValue(d.id, d.name)
            FROM Users d
            WHERE d.id IN (:idsList)
            """)
    List<SavedLongAndStringValue> findUserNameByIdsMaps(List<Long> idsList);

    boolean existsByPhone(String phone);
}