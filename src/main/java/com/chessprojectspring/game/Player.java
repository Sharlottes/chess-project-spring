package com.chessprojectspring.game;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    // uid
    private Long uid; // User.uid

    // 플레이어의 남은 시간 (ms)
    @Builder.Default
    private AtomicLong timeLeft = new AtomicLong(1800000); //1800000 30분

    // 추가시간 제공 메소드
    public void addTime(long timeToAdd) {
        timeLeft.addAndGet(timeToAdd);
    }
}
