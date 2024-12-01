package com.chessprojectspring.websocket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Setter
@Getter
public class Player {

    // uid
    private Long uid; // User.uid

    // 게임 시작 시간
    private long gameStartTime;

    // 플레이어의 남은 시간
    private volatile int timeLeft = 1800; // 30분
    private final int timeToAddEveryTurnStart = 5; // 5초

    // 현재 나의 턴인지 아닌지 (스레드 안전하게 접근하기 위해 AtomicBoolean 사용)
    private AtomicBoolean isMyTurn = new AtomicBoolean(false);

    // 추가시간 제공 메소드
    public void addTime() {
        timeLeft += timeToAddEveryTurnStart;
    }
    
    // 플레이어의 남은 시간이 종료되면 게임 종료하는 메소드 호출
    @Scheduled(fixedRate = 100) // 0.1초 마다 실행
    public void checkTimeLeft() {
        // 플레이어의 남은 시간이 종료되면 게임 종료하는 메소드 호출

        // 현재 나의 턴이면 
        if(isMyTurn.get()) {
            Long currentTime = System.currentTimeMillis();
            if(currentTime - gameStartTime > timeLeft * 1000) {
                // 게임 종료 메소드 호출
            }
        }

    }
}