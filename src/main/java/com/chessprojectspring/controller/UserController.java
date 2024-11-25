package com.chessprojectspring.controller;

import com.chessprojectspring.dto.auth.LoginResponse;
import com.chessprojectspring.dto.auth.LoginRequest;
import com.chessprojectspring.dto.auth.SignUpRequest;
import com.chessprojectspring.dto.auth.SignUpResponse;
import com.chessprojectspring.model.User;
import com.chessprojectspring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "User sign up", description = "Signs up a user with username, password, and nickname")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sign up successful"),
        @ApiResponse(responseCode = "400", description = "Nickname or Username already exists"),
        // 500번대는 서버 에러 (문자열 길이가 너무 길거나 너무 짧은 경우 등)
        @ApiResponse(responseCode = "500", description = "Username or Nickname or Password is too long or too short"),
        @ApiResponse(responseCode = "500", description = "Any other server error")
    })
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = userService.signUp(signUpRequest);

        // Sign up successful
        if (signUpResponse.getMessage().equals("Sign up successful")) {
            return ResponseEntity.ok(signUpResponse);
        }
        // Nickname or Username already exists
        return ResponseEntity.status(400).body(signUpResponse);
    }

    @Operation(summary = "User login", description = "Logs in a user with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Incorrect password"),
        @ApiResponse(responseCode = "404", description = "Username does not exist")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        if (loginResponse.getSessionId() != null) {
            return ResponseEntity.ok(loginResponse);
        } else if ("Incorrect password".equals(loginResponse.getMessage())) {
            return ResponseEntity.status(401).body(loginResponse);
        } else {
            return ResponseEntity.status(404).body(loginResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<User> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        User updatedUser = userService.updatePassword(id, newPassword);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/nickname")
    public ResponseEntity<User> updateNickname(@PathVariable Long id, @RequestBody String newNickname) {
        User updatedUser = userService.updateNickname(id, newNickname);
        return ResponseEntity.ok(updatedUser);
    }
}