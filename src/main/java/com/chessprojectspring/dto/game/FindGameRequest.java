package com.chessprojectspring.dto.game;

import lombok.Getter;

@Getter
public class FindGameRequest {
    
    private Long uid;

    // 게임 시간 룰 정보
    // 남은 시간
    private int timeLeft = 1800; // 30분
    // 매 턴 시작 시 추가되는 시간
    private int timeToAddEveryTurnStart = 5; // 5초
}
