package com.chessprojectspring.repository;

import com.chessprojectspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 추가적인 쿼리 메소드가 필요하면 여기에 정의

    // username으로 유저 찾기
    Optional<User> findByUserName(String userName);
    
    // 유저 전체 조회
    List<User> findAll();

    // username 존재 여부 확인
    boolean existsByUserName(String userName);

    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);

    // 필요없는 값을 비워 리턴. getOpponent
    User getOpponent(Long uid);
}
