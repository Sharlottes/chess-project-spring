package com.chessprojectspring.websocket;

import com.chessprojectspring.game.GameRoom;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeSession(Long userId) {
        sessions.remove(userId);
    }

    public void sendMessageToSession(Long userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            synchronized (session) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    // 예외 처리 로직
                }
            }
        }
    }

    public synchronized void createGameRoom() {
        // 게임룸 생성 로직
    }

    public synchronized void startGame(GameRoom gameRoom) {
        // 게임 시작 로직
    }
} 