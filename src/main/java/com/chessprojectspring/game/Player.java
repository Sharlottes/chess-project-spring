package com.chessprojectspring.game;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    // uid
    private Long uid; // User.uid

    // 플레이어의 남은 시간
    private AtomicInteger timeLeft = new AtomicInteger(1800); // 30분

    // 추가시간 제공 메소드
    public void addTime(int timeToAdd) {
        timeLeft.addAndGet(timeToAdd);
    }
}
