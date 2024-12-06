package com.chessprojectspring.dto.game;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveRequest {
    Long uid;
    String move;
}