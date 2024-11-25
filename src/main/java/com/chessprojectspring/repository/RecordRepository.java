package com.chessprojectspring.repository;

import com.chessprojectspring.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
    // 추가적인 쿼리 메소드가 필요하면 여기에 정의
}