package com.chessprojectspring.controller;

import com.chessprojectspring.dto.AuthResponse;
import com.chessprojectspring.dto.LoginRequest;
import com.chessprojectspring.dto.SignUpRequest;
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

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        AuthResponse authResponse = userService.signUp(signUpRequest);
        if (authResponse.getSessionId() != null) {
            return ResponseEntity.ok(authResponse);
        }
        // Nickname or Username already exists
        return ResponseEntity.status(400).body(authResponse);
    }

    @Operation(summary = "User login", description = "Logs in a user with username and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Incorrect password"),
        @ApiResponse(responseCode = "404", description = "Username does not exist")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = userService.login(loginRequest);
        if (authResponse.getSessionId() != null) {
            return ResponseEntity.ok(authResponse);
        } else if ("Incorrect password".equals(authResponse.getMessage())) {
            return ResponseEntity.status(401).body(authResponse);
        } else {
            return ResponseEntity.status(404).body(authResponse);
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