package com.chessprojectspring.service;

import com.chessprojectspring.model.User;
import com.chessprojectspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User signUp(User user) {
        // 회원가입 로직 구현
        return userRepository.save(user);
    }

    public boolean login(User user) {
        // 로그인 로직 구현
        return userRepository.existsByUserName(user.getUserName());
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