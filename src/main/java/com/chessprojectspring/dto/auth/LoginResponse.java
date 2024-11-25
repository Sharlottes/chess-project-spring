package com.chessprojectspring.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    // Getters and Setters
    private String message;
    private String sessionId;

    public LoginResponse(String message, String sessionId) {
        this.message = message;
        this.sessionId = sessionId;
    }
}