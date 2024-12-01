package com.chessprojectspring.game;

import com.github.bhlangonijr.chesslib.Board;
import org.springframework.web.socket.WebSocketSession;

public class GameRoom {

    private final Board board = new Board();

    private final Player playerWhite; // 백돌이 먼저 시작
    private final Player playerBlack;

    // turn 변수는 현재 턴을 나타내는 변수
    private int turn = 0; // 0은 백, 1은 흑

    // 게임 시작 시 주워지는 시간
    private final int timeToStartGame = 1800; // 30분

    // 매 턴 시작 시 추가되는 시간
    private final int timeToAddEveryTurnStart = 5; // 5초


    public GameRoom(Player playerWhite, Player playerBlack) {
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        initializeGame();
    }

    // 게임 시작 시 초기화 메서드
    public void initializeGame() {
        // 게임 시작 시 초기화 로직
        board.reset();
    }

    // 게임 진행 상태 관리 메서드
} 