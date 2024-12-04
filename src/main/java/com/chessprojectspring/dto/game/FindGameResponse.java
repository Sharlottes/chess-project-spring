package com.chessprojectspring.dto.game;

import com.chessprojectspring.model.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindGameResponse {
    private String type; // 메시지 타입
    private String message; // 매칭 결과 메시지
    private String color; // 플레이어의 색상 ("black" or "white")
    private User opponent; // 상대방 유저 정보
}
