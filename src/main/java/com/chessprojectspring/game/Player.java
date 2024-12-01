package com.chessprojectspring.game;

import lombok.Getter;
import lombok.Setter;
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
public class Player {

    // uid
    private Long uid; // User.uid

    // 게임 시작 시간
    private long gameStartTime;
    // 스레드 안전하게 접근하기 위해 AtomicLong 사용
    private AtomicLong myTurnStartTime = new AtomicLong(0);

    // 플레이어의 남은 시간
    private AtomicInteger timeLeft = new AtomicInteger(1800); // 30분
    private final int timeToAddEveryTurnStart = 5; // 5초

    // 현재 나의 턴인지 아닌지 (스레드 안전하게 접근하기 위해 AtomicBoolean 사용)
    private AtomicBoolean isMyTurn = new AtomicBoolean(false);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;

    public Player(Long uid) {
        this.uid = uid;
    }

    // 외부에서 시작해 줘야 함. 
    public void startScheduler() {
        scheduledFuture = scheduler.scheduleAtFixedRate(this::checkTimeLeft, 0, 100, TimeUnit.MILLISECONDS);
    }

    // 추가시간 제공 메소드
    public void addTime() {
        timeLeft.addAndGet(timeToAddEveryTurnStart);
    }
    
    // 플레이어의 남은 시간이 종료되면 게임 종료하는 메소드 호출
    public void checkTimeLeft() {
        // 플레이어의 남은 시간이 종료되면 게임 종료하는 메소드 호출

        // 현재 나의 턴이면 
        if(isMyTurn.get()) {
            long currentTime = System.currentTimeMillis();
            if(currentTime - myTurnStartTime.get() > timeLeft.get() * 1000) {
                // 게임 종료 메소드 호출
                sendGameOverMessage();
            }
        }
    }

    private void sendGameOverMessage() {
        String destination = "/topic/game-over/" + uid;
        String message = "게임 시간이 종료되었습니다.";
        simpMessagingTemplate.convertAndSend(destination, message);
    }

    // 외부에서 중지해 줘야 함. 
    public void stopScheduler() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    // 턴 변경 메서드
    public void myTurn() {
        this.isMyTurn.set(true);
        this.myTurnStartTime.set(System.currentTimeMillis());
    }

    public void notMyTurn() {
        this.isMyTurn.set(false);
    }
}
