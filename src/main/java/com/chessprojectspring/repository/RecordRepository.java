package com.chessprojectspring.repository;

import com.chessprojectspring.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> { }