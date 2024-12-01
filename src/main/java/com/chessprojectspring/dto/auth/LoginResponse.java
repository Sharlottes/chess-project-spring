package com.chessprojectspring.dto.auth;

import com.chessprojectspring.model.User;
import lombok.*;

@Data
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder // 빌더 패턴을 사용하는 메소드 자동 생성
public class LoginResponse {
    // Getters and Setters
    private String message;
    private String sessionId;
    private Long uid;
    private String userName;
    private String password;
    private String nickname;

    public LoginResponse(String message, String sessionId, User user) {
        this.message = message;
        this.sessionId = sessionId;
        this.uid = user.getUid();
        this.userName = user.getUserName();
        this.nickname = user.getNickname();
    }

    public LoginResponse(String message) {
        this.message = message;
    }
}