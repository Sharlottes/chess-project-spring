package com.chessprojectspring.controller;

import com.chessprojectspring.dto.game.MoveRequest;
import com.chessprojectspring.repository.WaitingQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.chessprojectspring.game.GameRoom;
import com.chessprojectspring.game.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import com.chessprojectspring.repository.GameRoomRepository;
import com.chessprojectspring.dto.game.FindGameRequest;
import com.chessprojectspring.dto.game.FindGameResponse;
import com.chessprojectspring.model.User;
import com.chessprojectspring.repository.UserRepository;
import com.chessprojectspring.service.GameService;
import com.chessprojectspring.service.UserService;
import com.chessprojectspring.dto.TypeMessageDTO;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Square;
import com.chessprojectspring.dto.game.MoveResponse;

@Controller
@EnableAsync
@Slf4j
public class GameController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WaitingQueueRepository waitingQueueRepository;
    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public GameController(SimpMessagingTemplate simpMessagingTemplate,
                          WaitingQueueRepository waitingQueueRepository,
                          GameRoomRepository gameRoomRepository,
                          UserRepository userRepository,
                          UserService userService,
                          GameService gameService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.waitingQueueRepository = waitingQueueRepository;
        this.gameRoomRepository = gameRoomRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.gameService = gameService;
    }

    @MessageMapping("/send")
    @SendTo("/sub/messages")
    public String sendMessage(String message) {
        log.debug(message);

        simpMessagingTemplate.convertAndSend("/sub/messages", message);
        
        return message;
    }

    // 게임 찾기 메소드
    // 클라이언트가 /pub/find-game 으로 자신의 uid를 보내면 호출되는 메소드d
    // 클라이언트측 발행 path : /pub/find-game
    // 클라이언트측 구독 path : /sub/find-game
    @MessageMapping("/find-game")
    @Async // 비동기 처리
    public void findGame(FindGameRequest findGameRequest) {
        Long uid1; // 이전에 대기큐에 있던 사람의 uid
        Long uid2 = findGameRequest.getUid(); // 지금 들어온 사람의 uid

        //TODO 제거하기 (테스트용도)
        //userService.increaseDrawCount(uid2);
        //userService.increaseWinCount(uid2);

        String destination = "/sub/find-game/" + uid2;

        // 존재하는 uid 인지 확인
        if (!userRepository.existsById(uid2)) {
            simpMessagingTemplate.convertAndSend(destination,
                new TypeMessageDTO("error", "존재하지 않는 uid입니다."));
            return;
        }

        // 이미 대기 중인 상태인지 검사
        if (gameService.isAlreadyReady(uid2)) {
            simpMessagingTemplate.convertAndSend(destination,
                    new TypeMessageDTO("waiting", "이미 대기 중인 상태입니다."));
            return;
        }

        // 해당 uid 가 이미 게임 중인 경우
        if (gameRoomRepository.isInGame(uid2)) {
            simpMessagingTemplate.convertAndSend(destination,
                    new TypeMessageDTO("error", "이미 게임 중인 상태입니다."));
            return;
        }

        // 게임 대기 중인 사람이 있는지 확인
        if (gameService.readyToPlay()) {
            
            uid1 = gameService.startGame(findGameRequest);

            // FindGameResponse 객체 생성
            FindGameResponse findGameResponse1 = FindGameResponse.builder()
                .type("game-start")
                .message("게임이 시작되었습니다.")
                .color("white")
                .opponent(userService.getOpponent(uid2)) // 상대방 이므로 uid2
                .build();

            FindGameResponse findGameResponse2 = FindGameResponse.builder()
                    .type("game-start")
                    .message("게임이 시작되었습니다.")
                    .color("black")
                    .opponent(userService.getOpponent(uid1)) // 상대방 이므로 uid1
                    .build();

            // 게임 시작 메시지 전송
            simpMessagingTemplate.convertAndSend("/sub/find-game/" + uid1, findGameResponse1);
            simpMessagingTemplate.convertAndSend("/sub/find-game/" + uid2, findGameResponse2);

        } else { // 게임 대기 중인 사람이 없는 경우
            simpMessagingTemplate.convertAndSend(destination, 
                new TypeMessageDTO("waiting", "매칭 상대 찾는 중"));

            gameService.ready(uid2);

            // 대기 시간 설정
            try {
                Thread.sleep(30000); // 30초 대기
                //Thread.sleep(5000); // 5초 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            gameService.unready(uid2);

            // 이미 진행중인 게임이 있다면 매칭실패 메시지 전송안함
            if (gameRoomRepository.isInGame(uid2)) {
                return;
            }
            
            simpMessagingTemplate.convertAndSend(destination, 
                new TypeMessageDTO("fail", "매칭에 실패했습니다. 다시 시도해 주세요."));
            return;
        }
    }

    // 체스 말을 움직이는 메소드 
    // 클라이언트측 발행 path : /pub/move
    // 클라이언트측 구독 path : /sub/move
    @MessageMapping("/move")
    public void movePiece(MoveRequest moveRequest) {
        Long uid = moveRequest.getUid();
        GameRoom gameRoom = gameRoomRepository.getGameRoomByUid(uid);
        String san = moveRequest.getMove().replaceAll(" ", "");

        if (gameRoom == null) {
            simpMessagingTemplate.convertAndSend("/sub/move/" + uid, 
                new TypeMessageDTO("error", "진행중인 게임을 찾을 수 없습니다."));
            return;
        }

        Long currentTurnUid = gameRoom.getCurrentTurnUid();

        if (!currentTurnUid.equals(uid)) {
            simpMessagingTemplate.convertAndSend("/sub/move/" + uid, 
                new TypeMessageDTO("error", "본인의 턴이 아닙니다."));
            return;
        }

        // 체스 말 움직임 실행
        gameRoom.move(san);
    }
}
