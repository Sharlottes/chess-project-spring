package com.chessprojectspring.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder // 빌더 패턴을 사용하는 메소드 자동 생성
public class SignUpResponse {
    private String message;
    private String userName;
    private String nickname;
    private String password;
}
