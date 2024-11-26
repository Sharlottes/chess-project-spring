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
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
        
        // Java 14 Switch 표현식
        return switch (loginResponse.getMessage()) {
            case "Login successful" -> ResponseEntity.ok(loginResponse);
            case "Incorrect password" -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            case "Username does not exist" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(loginResponse);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loginResponse);
        };
    }

    @Operation(summary = "Delete user account", description = "Deletes a user account if authenticated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access - User not logged in"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete other user's account"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, HttpSession session) {
        // 세션에서 사용자 이름 가져오기
        String sessionUserName = (String) session.getAttribute("userName");

        // 세션에 사용자 이름이 없으면 401 에러 반환
        if (sessionUserName == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }
        
        // 사용자 삭제 try-catch
        try {
            userService.deleteUser(id, sessionUserName);
            return ResponseEntity.ok("User deleted successfully");
        } catch (IllegalArgumentException e) {
            
            return switch (e.getMessage()) {
                case "User not found" -> ResponseEntity.status(404).body(e.getMessage());
                case "Unauthorized access" -> ResponseEntity.status(403).body(e.getMessage());
                default -> ResponseEntity.status(500).body("Internal server error");
            };
        }
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