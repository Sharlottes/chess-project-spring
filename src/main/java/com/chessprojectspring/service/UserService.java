package com.chessprojectspring.service;

import com.chessprojectspring.dto.auth.LoginResponse;
import com.chessprojectspring.dto.auth.LoginRequest;
import com.chessprojectspring.dto.auth.SignUpRequest;
import com.chessprojectspring.dto.auth.SignUpResponse;
import com.chessprojectspring.model.Record;
import com.chessprojectspring.model.User;
import com.chessprojectspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUserName())) {
            return new SignUpResponse("Username already exists");
        }
        if (userRepository.existsByNickname(signUpRequest.getNickname())) {
            return new SignUpResponse("Nickname already exists");
        }

        User user = User.builder()
                .userName(signUpRequest.getUserName())
                .password(signUpRequest.getPassword())
                .nickname(signUpRequest.getNickname())
                .build();

        Record record = Record.builder()
                .user(user)
                .build();

        user.setRecord(record);

        User savedUser = userRepository.save(user);
        session.setAttribute("userName", savedUser.getUserName());

        //return new LoginResponse("Sign up successful", session.getId());
        // 회원가입 후 로그인 해야 하므로 sessionId는 넘겨줄 필요 없음
        return new SignUpResponse("Sign up successful");
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // userName으로 사용자 조회
        Optional<User> userOptional = userRepository.findByUserName(loginRequest.getUserName());

        if (!userOptional.isPresent()) {
            // userName이 존재하지 않는 경우
            return new LoginResponse("Username does not exist", null);
        }

        User user = userOptional.get();
        // 비밀번호 확인
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            // 비밀번호가 틀린 경우
            return new LoginResponse("Incorrect password", null);
        }

        // 비밀번호가 맞으면 세션에 userName 저장
        session.setAttribute("userName", user.getUserName());
        // 로그인 성공 응답
        return new LoginResponse("Login successful", session.getId());
    }

    public void deleteUser(Long id) {
        // 회원탈퇴 로직 구현
        userRepository.deleteById(id);
    }

    public User updatePassword(Long id, String newPassword) {
        // 비밀번호 변경 로직 구현
        User user = userRepository.findById(id).orElseThrow();
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    public User updateNickname(Long id, String newNickname) {
        // 닉네임 변경 로직 구현
        User user = userRepository.findById(id).orElseThrow();
        user.setNickname(newNickname);
        return userRepository.save(user);
    }
}