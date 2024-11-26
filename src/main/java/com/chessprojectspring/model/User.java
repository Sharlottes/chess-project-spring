package com.chessprojectspring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity // 엔티티 클래스임을 명시
@Data // 모든 필드의 getter, setter, toString, equals, hashCode 메소드를 자동으로 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder // 빌더 패턴을 사용하는 메소드 자동 생성
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false) 
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private Record record;
}
