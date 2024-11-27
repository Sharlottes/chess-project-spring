package com.chessprojectspring.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자 자동 생성
public class SignUpResponse {
    // Getters and Setters
    private String message;
}