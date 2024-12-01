package com.chessprojectspring.game;

import com.github.bhlangonijr.chesslib.Board;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Builder;

@Component
@Getter
@Setter
@Builder
@AllArgsConstructor
public class GameRoom {

    // 게임룸 고유 id
    private Long id;
    
    private final Board board = new Board();

    private Player playerWhite; // 백돌이 먼저 시작
    private Player playerBlack;

    // turn 변수는 현재 턴을 나타내는 변수 (스레드 안전하게 접근하기 위해 AtomicInteger 사용)
    private final AtomicBoolean isSnooze = new AtomicBoolean(false);
    private AtomicInteger turn = new AtomicInteger(0); // 0:백, 1:흑

    // 시간 관련 변수
    // 게임 시작 시간
    private long gameStartTime;

    // 게임 시작 시 주워지는 시간
    @Builder.Default
    private int timeToStartGame = 1800; // 30분

    // 매 턴 시작 시 추가되는 시간
    @Builder.Default
    private int timeToAddEveryTurnStart = 5; // 5초

    // 가장 최근 턴 시작 시간
    private AtomicLong latestTurnStartTime = new AtomicLong(0);

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public GameRoom(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // 게임 시작 시 초기화 메서드
    public void initializeGame() {
        // 게임 시작 시 초기화 로직
        //board.reset();
    }

    // 게임 시작 메소드
    public void startGame() {
        gameStartTime = System.currentTimeMillis();
        startScheduler();
    }

    // snooze 대기하는 메소드
    public void snooze() {
        isSnooze.set(true);
    }

    // 턴 변경 메소드
    public void changeTurn() {
        isSnooze.set(false);
        turn.set(turn.addAndGet(1) % 2); // 현재 0이면 1, 1이면 0 으로 변경
        if(turn.get() == 0) {
            playerWhite.addTime(timeToAddEveryTurnStart);
        } else {
            playerBlack.addTime(timeToAddEveryTurnStart);
        }
    }

    // 스케줄링 관련 메소드 여기로 옮기기
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;

    // 스케줄링 시작 메소드
    public void startScheduler() {
        scheduledFuture = scheduler.scheduleAtFixedRate(this::checkTimeLeft, 0, 100, TimeUnit.MILLISECONDS);
    }

    // 플레이어의 남은 시간이 종료되면 게임 종료하는 메소드 호출
    public void checkTimeLeft() {
        // 플레이어의 남은 시간이 종료되면 게임 종료하는 메소드 호출

        // 현재 snooze 상태면 무시
        if(isSnooze.get()) {
            return;
        }

        int playerTimeLeft;

        // 현재 턴이 백이면 백 플레이어의 남은 시간 체크
        if(turn.get() == 0) {
            playerTimeLeft = playerWhite.getTimeLeft().get();
        } else {
            playerTimeLeft = playerBlack.getTimeLeft().get();
        }

        // 현재 시간에서 가장 최근 턴 시작 시간을 뺀 시간이 플레이어의 남은 시간보다 크면 게임 종료
        if(System.currentTimeMillis() - latestTurnStartTime.get() > playerTimeLeft) {
            // 누구의 시간이 종료되었는지 알려주기 위해 turn 변수 전달
            sendGameOverMessage(turn.get()); 
            stopScheduler();
        }
    }

    // 스케줄링 중지 메소드
    public void stopScheduler() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    // 게임 종료 메소드
    private void sendGameOverMessage(int turn) {
        String destination1 = "/game/game-over/" + playerWhite.getUid();
        String destination2 = "/game/game-over/" + playerBlack.getUid();
        String message;
        if(turn == 0) {
            message = "백 플레이어의 시간이 종료되었습니다.";
        } else {
            message = "흑 플레이어의 시간이 종료되었습니다.";
        }
        simpMessagingTemplate.convertAndSend(destination1, message);
        simpMessagingTemplate.convertAndSend(destination2, message);
    }

    // 누가 이겼는지 반환하는 메소드
    public String getWinner() {
        
    }
} 