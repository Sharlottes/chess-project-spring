package com.chessprojectspring.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpRequest {

    // Getters and Setters
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String userName;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "Nickname is mandatory")
    @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
    private String nickname;

}