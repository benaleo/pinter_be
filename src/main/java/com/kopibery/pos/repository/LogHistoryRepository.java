package com.kopibery.pos.repository;

import com.kopibery.pos.entity.LogHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogHistoryRepository extends JpaRepository<LogHistory, Long> {

}