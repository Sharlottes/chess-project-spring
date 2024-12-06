package com.chessprojectspring.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.chessprojectspring.model.Record;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameOverResponse {
    private String message;
    private String gameResult; // win | lose | draw
    private String type; // timeover | surrender | checkmate | checkstalemate
    private Record record; // 자신의 게임 전적
}
