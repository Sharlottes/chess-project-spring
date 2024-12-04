package com.chessprojectspring.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameOverResponse {
    private String message; // 게임 종료 메시지
    private String winner; // 이긴 플레이어
    private boolean isDraw; // 무승부 여부
    private boolean timeover; // 시간 초과 여부
    private boolean surrender; // 항복 여부
    private boolean checkmate; // 체크메이트 여부
    private Record record; // 게임 전적
}
