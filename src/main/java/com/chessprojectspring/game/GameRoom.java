package com.chessprojectspring.game;

import org.springframework.web.socket.WebSocketSession;

public class GameRoom {

    private final WebSocketSession player1Session;
    private final WebSocketSession player2Session;

    // 게임 시작 시 주워지는 시간
    private final int timeToStartGame = 1800; // 30분

    // 매 턴 시작 시 추가되는 시간
    private final int timeToAddEveryTurnStart = 5; // 5초


    public GameRoom(WebSocketSession player1Session, WebSocketSession player2Session) {
        this.player1Session = player1Session;
        this.player2Session = player2Session;
    }

    // 게임 진행 상태 관리 메서드
} 