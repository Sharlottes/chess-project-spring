package com.chessprojectspring.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpResponse {
    // Getters and Setters
    private String message;

    public SignUpResponse(String message) {
        this.message = message;
    }
}