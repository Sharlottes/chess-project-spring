package com.chessprojectspring.service;

import com.chessprojectspring.model.User;
import com.chessprojectspring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void increaseWinCount() {
        // 예상값 설정
        int expectedWinCount = 1;
        int expectedLossCount = 0;
        int expectedDrawCount = 0;

        // 실제값 설정
        userService.increaseWinCount(9L);

        // 검증
        assertEquals(expectedWinCount, userRepository.findById(9L).orElseThrow().getRecord().getWins());
    }

    @Test
    void increaseLossCount() {
    }

    @Test
    void increaseDrawCount() {
    }
}