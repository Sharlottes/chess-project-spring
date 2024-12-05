package com.chessprojectspring.service;

import com.chessprojectspring.dto.auth.*;
import com.chessprojectspring.model.Record;
import com.chessprojectspring.model.User;
import com.chessprojectspring.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    // @Autowired
    // private UserRepository userRepository;

    // @Autowired
    // private HttpSession session;

    // spring 생성자 주입 권장
    private final UserRepository userRepository;
    private final HttpSession session;

    public UserService(UserRepository userRepository, HttpSession session) {
        this.userRepository = userRepository;
        this.session = session;
    }

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

        //return new LoginResponse("Sign up successful", session.getUid());
        // 회원가입 후 로그인 해야 하므로 sessionId는 넘겨줄 필요 없음
        return new SignUpResponse("Sign up successful");
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // userName으로 사용자 조회
        Optional<User> userOptional = userRepository.findByUserName(loginRequest.getUserName());

        if (!userOptional.isPresent()) {
            // userName이 존재하지 않는 경우
            return new LoginResponse("Username does not exist");
        }

        User user = userOptional.get();
        // 비밀번호 확인
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            // 비밀번호가 틀린 경우
            return new LoginResponse("Incorrect password");
        }

        // 비밀번호가 맞으면 세션에 userName 저장
        session.setAttribute("userName", user.getUserName());

        // 현재 존재하는 모든 세션 정보를 로그에 출력
        List<String> attributeNames = Collections.list(session.getAttributeNames());
        attributeNames.forEach(name -> {
            log.debug("Session name: {}, value: {}", name, session.getAttribute(name));
        });

        // 로그인 성공 응답
        return new LoginResponse("Login successful", session.getId(), user);
    }

    // 트랜잭션 관리: 매서드 내의 모든 작업이 성공적으로 완료되어야 커밋됨. 
    // 예외 발생 시 처음으로 롤백하여 다시 실행. (데이터 일관성 유지)
    @Transactional 
    public void deleteUser(Long id, DeleteUserRequest deleteUserRequest, String sessionUserName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getUserName().equals(sessionUserName)) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        if (!user.getPassword().equals(deleteUserRequest.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        userRepository.deleteById(id);
        session.invalidate(); // 세션 무효화
    }

    public User updatePassword(Long id, EditPwdRequest editPwdRequest, String sessionUserName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getUserName().equals(sessionUserName)) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        if (!user.getPassword().equals(editPwdRequest.getOldPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        user.setPassword(editPwdRequest.getNewPassword());
        return userRepository.save(user);
    }

    public User updateNickname(Long id, EditNicknameRequest editNicknameRequest, String sessionUserName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getUserName().equals(sessionUserName)) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        if (!user.getPassword().equals(editNicknameRequest.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        user.setNickname(editNicknameRequest.getNewNickname());
        return userRepository.save(user);
    }

    public User 
    getOpponent(Long uid) {
        Optional<User> userOptional = userRepository.findById(uid);
        if (!userOptional.isPresent()) {
            return null;
        }

        User user = userOptional.get();
        user.setUid(null);
        user.setUserName(null);
        user.setPassword(null);
        user.getRecord().setId(null);
        user.getRecord().setUser(null);

        return user;
    }

    // 유저 승리 횟수 증가
    public void increaseWinCount(Long uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.getRecord().setWins(user.getRecord().getWins() + 1);
        userRepository.save(user);
    }

    // 유저 패배 횟수 증가
    public void increaseLossCount(Long uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.getRecord().setLosses(user.getRecord().getLosses() + 1);
        userRepository.save(user);
    }

    // 유저 무승부 횟수 증가
    public void increaseDrawCount(Long uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.getRecord().setDraws(user.getRecord().getDraws() + 1);
        userRepository.save(user);
    }
}
