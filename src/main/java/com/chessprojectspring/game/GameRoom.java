package com.chessprojectspring.game;

import com.github.bhlangonijr.chesslib.Board;

import com.github.bhlangonijr.chesslib.move.Move;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import com.chessprojectspring.repository.GameRoomRepository;
import com.github.bhlangonijr.chesslib.Side;
import com.chessprojectspring.dto.game.GameOverResponse;
import com.chessprojectspring.dto.game.MoveResponse;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Setter
@Builder
@AllArgsConstructor
@Slf4j
public class GameRoom {

    // 게임룸 고유 id
    private long id;
    
    @Builder.Default
    private Board board = new Board();

    @Builder.Default
    private Player playerWhite = new Player(); // 백돌이 먼저 시작
    @Builder.Default
    private Player playerBlack = new Player();

    // turn 변수는 현재 턴을 나타내는 변수 (스레드 안전하게 접근하기 위해 AtomicInteger 사용)
    @Builder.Default
    private AtomicBoolean isSnooze = new AtomicBoolean(false);
    @Builder.Default
    private AtomicInteger turn = new AtomicInteger(0); // 0:백, 1:흑

    // 시간 관련 변수
    // 게임 시작 시간
    private long gameStartTime;

    // 게임 시작 시 주워지는 시간
    @Builder.Default
    private long timeToStartGame = 1800000; // 30분

    // 매 턴 시작 시 추가되는 시간
    @Builder.Default
    private long timeToAddEveryTurnStart = 10000; // 10초

    // 가장 최근 턴 시작 시간
    @Builder.Default
    private AtomicLong latestTurnStartTime = new AtomicLong(0);

    private SimpMessagingTemplate simpMessagingTemplate;
    private GameRoomRepository gameRoomRepository;

    @Autowired
    public GameRoom(SimpMessagingTemplate simpMessagingTemplate, 
                    GameRoomRepository gameRoomRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameRoomRepository = gameRoomRepository;
    }

    // 게임 시작 시 초기화 메서드
    public void initGame(long timeToStartGame, long timeToAddEveryTurnStart) {
        this.timeToStartGame = timeToStartGame;
        this.timeToAddEveryTurnStart = timeToAddEveryTurnStart;
        initGame();
    }
    public void initGame() {
        log.debug("[GameRoom{}-initGame] Time to start game: {}, Time to add every turn start: {}", timeToStartGame, timeToAddEveryTurnStart);
        playerWhite.getTimeLeft().set(timeToStartGame);
        playerBlack.getTimeLeft().set(timeToStartGame);
    }

    // 게임 시작 메소드
    public void startGame() {
        initGame();
        gameStartTime = System.currentTimeMillis();
        latestTurnStartTime.set(System.currentTimeMillis());
        startScheduler();
    }

    // 스케줄러 위해 턴 변경 & 턴 추가시간 부여
    public void changeTurnForScheduler() {
        if(getCurrentTurn() == Side.WHITE) {
            playerWhite.addTime(timeToAddEveryTurnStart);
            turn.set(0);
        } else {
            playerBlack.addTime(timeToAddEveryTurnStart);
            turn.set(1);
        }
        latestTurnStartTime.set(System.currentTimeMillis());
    }

    // 스케줄링 관련 메소드 여기로 옮기기
    @Builder.Default
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;

    // 스케줄링 시작 메소드
    public void startScheduler() {
        log.debug("[GameRoom{}-scheduler] Starting scheduler", id);
        // TODO 100ms 로 다시 바꾸기. (테스트 용으로 1000ms 로 설정)
        scheduledFuture = scheduler.scheduleAtFixedRate(this::checkTimeLeft, 0, 1000, TimeUnit.MILLISECONDS);
    }

    // 현재 시간
    @Builder.Default
    private long currentTime = System.currentTimeMillis();

    // 플레이어의 남은 시간이 종료되면 게임 종료하는 메소드 호출
    public void checkTimeLeft() {

        if(isSnooze.get()) {
            return;
        }
        
        //log.debug("[GameRoom{}-scheduler] Checking time left", id);

        long playerTimeLeft;

        // 현재 턴이 백이면 백 플레이어의 남은 시간 체크
        if(turn.get() == 0) {
            playerTimeLeft = playerWhite.getTimeLeft().get();
        } else {
            playerTimeLeft = playerBlack.getTimeLeft().get();
        }

        currentTime = System.currentTimeMillis();

        log.debug("--------------------------------");
        log.debug("[GameRoom{}-scheduler] Turn: {}, Time left: {}:{}", id, getCurrentTurn(), playerTimeLeft / 60000, (playerTimeLeft % 60000) / 1000);
        log.debug("current time: {}", currentTime);
        log.debug("latest turn start time: {}", latestTurnStartTime.get());
        log.debug("time difference: {}", currentTime - latestTurnStartTime.get());
        log.debug("\n");

        // 현재 시간에서 가장 최근 턴 시작 시간을 뺀 시간이 플레이어의 남은 시간보다 크면 게임 종료
        if(currentTime - latestTurnStartTime.get() > playerTimeLeft) {
            // 턴과 남은시간(분:초) 출력
            
            // 누구의 시간이 종료되었는지 알려주기 위해 turn 변수 전달
            log.debug("[GameRoom{}-checkTimeLeft] Time over 진입 전", id);
            timeOver();
        }
    }

    // 스케줄링 중지 메소드
    public void stopScheduler() {
        if (scheduledFuture != null) {
            log.debug("[GameRoom{}-scheduler] Stopping scheduler", id);
            scheduledFuture.cancel(false);
        }
    }

    // 타임오버 메소드
    private void timeOver() {
        log.debug("[GameRoom{}-timeOver] Time over 진입완료", id);

        log.debug("[GameRoom{}-timeOver] 스케줄러 중지 전", id);
        stopScheduler();
        log.debug("[GameRoom{}-timeOver] 스케줄러 중지 후", id);

        try {
            log.debug("[GameRoom{}-timeOver] 게임 룸 삭제 전", id);
            gameRoomRepository.removeGameRoom(id);
            log.debug("[GameRoom{}-timeOver] 게임 룸 삭제 후", id);
        } catch (Exception e) {
            log.error("[GameRoom{}-timeOver] 게임 룸 삭제 중 예외 발생: {}", id, e.getMessage());
        }

        String destinationWhite = "/sub/game-over/" + playerWhite.getUid();
        String destinationBlack = "/sub/game-over/" + playerBlack.getUid();

        GameOverResponse gameOverWhite;
        GameOverResponse gameOverBlack;

        if(getCurrentTurn() == Side.WHITE) { // 백 플레이어의 시간이 종료되었으면
            gameOverWhite = GameOverResponse.builder()
                    .message("백 플레이어의 시간이 종료되었습니다.")
                    .gameResult("lose")
                    .type("timeover")
                    .build();
            gameOverBlack = GameOverResponse.builder()
                    .message("백 플레이어의 시간이 종료되었습니다.")
                    .gameResult("win")
                    .type("timeover")
                    .build();
        } else { // 흑 플레이어의 시간이 종료되었으면
            gameOverWhite = GameOverResponse.builder()
                    .message("흑 플레이어의 시간이 종료되었습니다.")
                    .gameResult("win")
                    .type("timeover")
                    .build();
            gameOverBlack = GameOverResponse.builder()
                    .message("흑 플레이어의 시간이 종료되었습니다.")
                    .gameResult("lose")
                    .type("timeover")
                    .build();
        }
        simpMessagingTemplate.convertAndSend(destinationWhite, gameOverWhite);
        simpMessagingTemplate.convertAndSend(destinationBlack, gameOverBlack);

        // 게임 룸 삭제
        // stopScheduler(); // (순서 바꿔보기?)

        // log.debug("[GameRoom{}-timeOver] 게임 룸 삭제 전", id);
        // gameRoomRepository.removeGameRoom(id);
        // log.debug("[GameRoom{}-timeOver] 게임 룸 삭제 후", id);
        // stopScheduler();
        // log.debug("[GameRoom{}-timeOver] 스케줄러 중지 후", id);
    }

    // 현재 턴 반환 메소드
    public Side getCurrentTurn() {
        return board.getSideToMove();
    }

    public Long getCurrentTurnUid() {
        return getCurrentTurn() == Side.WHITE ? playerWhite.getUid() : playerBlack.getUid();
    }

    // move 메소드
    public void move(String san) {
        // SAN 형식의 string을 MOVE 형식으로 변환
        Move move = new Move(san.replaceAll("=", ""), getCurrentTurn());

        // 움직임 유효성 검사
        boolean isValidMove;

        try {
            isValidMove = board.isMoveLegal(move, true);
        } catch (Exception e) {
            isValidMove = false;
        }

        if (isValidMove) {
            // 해당 move 가 정상적이므로, 이 로직이 실행되는동안 게임종료되는일이 없도록 snooze 설정
            isSnooze.set(true);

            // 남은시간 감소
            long timeSpent = System.currentTimeMillis() - latestTurnStartTime.get();

            if(getCurrentTurn() == Side.WHITE) {
                playerWhite.getTimeLeft().addAndGet(- timeSpent);
            } else {
                playerBlack.getTimeLeft().addAndGet(- timeSpent);
            }

            // 움직임 실행
            board.doMove(san);

            // 스케줄러 위해 턴 변경 & 턴 추가시간 부여
            changeTurnForScheduler();

            MoveResponse moveResponseWhite = MoveResponse.builder()
                    .type("success")
                    .message("move success")
                    .fen(board.getFen())
                    .move(san)
                    .turn(getCurrentTurn().value())
                    .timeLeft(playerWhite.getTimeLeft().get())
                    .build();

            MoveResponse moveResponseBlack = MoveResponse.builder()
                    .type("success")
                    .message("move success")
                    .fen(board.getFen())
                    .move(san)
                    .turn(getCurrentTurn().value())
                    .timeLeft(playerBlack.getTimeLeft().get())
                    .build();

            simpMessagingTemplate.convertAndSend("/sub/move/" + playerWhite.getUid(), moveResponseWhite);
            simpMessagingTemplate.convertAndSend("/sub/move/" + playerBlack.getUid(), moveResponseBlack);

            isSnooze.set(false);
        } else {
            //simpMessagingTemplate.convertAndSend("/sub/move/" + uid, "invalid move");
        }

        changeTurnForScheduler();



        isSnooze.set(false);
    }

    // 움직임 유효성 검사 메소드
    public boolean isValidMove(String move) {
        return true;
    }
} 
