package com.chessprojectspring.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    // Getters and Setters
    private String message;
    private String sessionId;

    public AuthResponse(String message, String sessionId) {
        this.message = message;
        this.sessionId = sessionId;
    }
}