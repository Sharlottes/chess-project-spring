package com.chessprojectspring.dto.game;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveResponse {
    String type;
    String message;
    String fen;
    String move;
    String turn;
    long timeLeft;
    long timeLeftOpponent;
}
