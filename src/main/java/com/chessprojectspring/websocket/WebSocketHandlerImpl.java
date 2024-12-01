package com.chessprojectspring.websocket;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private final SessionManager sessionManager;
    
    public WebSocketHandlerImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // 연결 성공 시 세션 추가
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionManager.addSession(session);
    }

    // 메시지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지 처리 로직
    }

    // 연결 종료 시 세션 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionManager.removeSession(session);
    }
} 