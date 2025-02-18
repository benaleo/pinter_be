package com.kopibery.pos.repository;

import com.kopibery.pos.entity.RlUserShift;
import com.kopibery.pos.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RlUserShiftRepository extends JpaRepository<RlUserShift, Long> {

    Optional<RlUserShift> findByUserAndDate(Users user, LocalDate localDate);
}
