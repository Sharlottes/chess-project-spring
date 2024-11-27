package com.chessprojectspring.util;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class HttpSessionUtil {
    
    /**
     * 세션 만료 시간을 갱신
     * @param session 갱신할 HttpSession
     */
    public void refreshSession(HttpSession session) {
        if (session != null) {
            // 기본 세션 타임아웃 시간으로 갱신
            session.setMaxInactiveInterval(1800); // 30분
        }
    }

    /**
     * 세션이 유효한지 확인
     * @param session 확인할 HttpSession
     * @return 세션 유효 여부
     */
    public boolean isSessionValid(HttpSession session) {
        try {
            if (session != null) {
                // 세션에서 userName 어트리뷰트 가져와서 null이 아닌지 확인
                return session.getAttribute("userName") != null;
            }
            return false;
        } catch (IllegalStateException e) {
            // 세션이 이미 무효화된 경우
            return false;
        }
    }
} 