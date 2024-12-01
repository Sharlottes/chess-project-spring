package com.chessprojectspring.controller;

import com.chessprojectspring.dto.Message;
import com.chessprojectspring.dto.game.MoveRequest;
import com.chessprojectspring.websocket.GameManager;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.chessprojectspring.game.GameRoom;
import com.chessprojectspring.game.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@Controller
@EnableAsync
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameManager gameManager;

    @Autowired
    public MessageController(SimpMessagingTemplate simpMessagingTemplate, GameManager gameManager) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameManager = gameManager;
    }

    @MessageMapping("/send")
    @SendTo("/match/messages")
    public Message sendMessage(Message message) {
        return message;
    }

    // 체스 말을 움직이는 메소드 
    // 클라이언트 구독 path : /topic/moves/{uid}
    @MessageMapping("/move")
    @SendTo("/game/moves")
    public void movePiece(MoveRequest moveRequest) {
        String destination = "/game/moves/" + moveRequest.getUid();
        simpMessagingTemplate.convertAndSend(destination, moveRequest);
    }

    // 게임 찾기 메소드
    // 클라이언트가 /find-game 으로 자신의 uid를 보내면 호출되는 메소드
    // 클라이언트 구독 path : /match/find-game/{uid}
    @MessageMapping("/find-game")
    @Async
    public void findGame(String uidStr) {
        Long uid = Long.parseLong(uidStr);
        String destination = "/match/find-game/" + uid;

        // 대기 큐에 자신을 추가하고 30초를 대기함.
        // 30초 동안 매칭되면 게임 룸 생성 후 /match/game-start/{uid} 로 게임 시작 메시지 전송.
        gameManager.addToWaitingQueue(uid);

        // 매칭 시도 횟수
        int attempts = 1;
        boolean matched = false;

        for (int i = 0; i < attempts; i++) {
            // 큐에 한명 이상 존재하면 한명을 꺼내서 본인과 매칭 
            if (gameManager.getWaitingQueueSize() >= 1) {

                Long matchedPlayer = gameManager.pollOneFromQueue();

                if (matchedPlayer != null) {
                    Long player1Uid = matchedPlayer;
                    Long player2Uid = uid;

                    // 게임 룸 생성
                    GameRoom gameRoom = new GameRoom(new Player(player1Uid), new Player(player2Uid));

                    // 게임 시작 메시지 전송
                    simpMessagingTemplate.convertAndSend("/match/game-start/" + player1Uid, "게임이 시작되었습니다.");
                    simpMessagingTemplate.convertAndSend("/match/game-start/" + player2Uid, "게임이 시작되었습니다.");
                    matched = true;
                    break;
                }
            } else {
                // 아직 매칭되지 않은 경우 대기 메시지 전송
                simpMessagingTemplate.convertAndSend(destination, "대기 중입니다. 잠시만 기다려 주세요.");

                // 대기 시간 설정 (예: 2초)
                try {
                    Thread.sleep(30000); // 30초 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (!matched) {
            simpMessagingTemplate.convertAndSend(destination, "매칭에 실패했습니다. 다시 시도해 주세요.");
        }
    }
}
