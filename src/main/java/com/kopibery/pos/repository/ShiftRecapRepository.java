package com.kopibery.pos.repository;

import com.kopibery.pos.entity.MsShift;
import com.kopibery.pos.entity.ShiftRecap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRecapRepository extends JpaRepository<ShiftRecap, Long> {

    ShiftRecap findByShift(MsShift shift);
}