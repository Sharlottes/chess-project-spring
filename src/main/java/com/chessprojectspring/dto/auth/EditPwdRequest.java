package com.chessprojectspring.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditPwdRequest {

    @NotBlank(message = "Old password is mandatory")
    @Size(min = 6, max = 20, message = "Old password must be between 6 and 20 characters")
    private String oldPassword;

    @NotBlank(message = "New password is mandatory")
    @Size(min = 6, max = 20, message = "New password must be between 6 and 20 characters")
    private String newPassword;
}
