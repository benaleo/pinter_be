package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.Otp;
import com.kasirpinter.pos.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Otp o SET o.valid = false WHERE o.user = :user")
    void invalidateOtpsForUser(Users user);

    Optional<Otp> findByUserAndOtpAndValidIsTrue(Users user, String otp);
}