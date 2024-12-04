package com.chessprojectspring.dto.game;

import lombok.Getter;

@Getter
public class FindGameRequest {
    
    private Long uid; // User의 uid

    // 게임 시간 룰 정보
    // 남은 시간
    private long timeLeft = 1800000; // 30분
    // 매 턴 시작 시 추가되는 시간
    private long timeToAddEveryTurnStart = 10000; // 10초
}
