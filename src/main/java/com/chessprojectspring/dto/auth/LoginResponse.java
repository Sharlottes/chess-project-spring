package com.chessprojectspring.dto.auth;

import com.chessprojectspring.model.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    // Getters and Setters
    private String message;
    private String sessionId;
    private Long uid;
    private String username;
    private String nickname;

    public LoginResponse(String message, String sessionId, User user) {
        this.message = message;
        this.sessionId = sessionId;
        this.uid = user.getUid();
        this.username = user.getUserName();
        this.nickname = user.getNickname();
    }

    public LoginResponse(String message) {
        this.message = message;
    }
}