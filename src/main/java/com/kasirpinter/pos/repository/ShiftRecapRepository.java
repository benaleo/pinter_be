package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.MsShift;
import com.kasirpinter.pos.entity.ShiftRecap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRecapRepository extends JpaRepository<ShiftRecap, Long> {

    ShiftRecap findByShift(MsShift shift);
}