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
    String turn; // white or black

    // 해당 무브가 캐슬링인지 아닌지 확인하는 변수
    boolean isCastling;
}