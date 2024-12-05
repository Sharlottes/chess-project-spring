package com.chessprojectspring.service;

import com.chessprojectspring.dto.TypeMessageDTO;
import com.chessprojectspring.dto.game.FindGameRequest;
import com.chessprojectspring.game.GameRoom;
import com.chessprojectspring.game.Player;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.chessprojectspring.repository.WaitingQueueRepository;
import com.chessprojectspring.repository.GameRoomRepository;

@Service
public class GameService {

    private final WaitingQueueRepository waitingQueueRepository;
    private final GameRoomRepository gameRoomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameService(WaitingQueueRepository waitingQueueRepository, 
                       GameRoomRepository gameRoomRepository,
                       SimpMessagingTemplate simpMessagingTemplate) {
        this.waitingQueueRepository = waitingQueueRepository;
        this.gameRoomRepository = gameRoomRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // 게임이 시작할 수 있는 상태인지 확인 
    public boolean readyToPlay() {
        return waitingQueueRepository.getWaitingQueueSize() >= 1;
    }

    public void ready(Long uid) {
        waitingQueueRepository.addToWaitingQueue(uid);
    }

    public void unready(Long uid) {
        waitingQueueRepository.removeFromWaitingQueue(uid);
    }

    // 이미 대기 중인 상태인지 검사
    public boolean isAlreadyReady(Long uid) {
        return waitingQueueRepository.isInWaitingQueue(uid);
    }

    /**
     * 게임 시작
     * @param findGameRequest
     * @return 플레이어 1의 uid (대기큐에 원래 있었던 user의 uid)
     */
    public Long startGame(FindGameRequest findGameRequest) {
        Long uid1 = waitingQueueRepository.pollOneFromQueue();
        Long uid2 = findGameRequest.getUid();

        // 게임 찾기 성공 메시지 전송 (게임이 시작 가능한 조건일 때 즉시 전송하기 위함)
        TypeMessageDTO typeMessageDTO = new TypeMessageDTO("game-found", "게임을 찾았습니다. 연결 준비중입니다.");
        simpMessagingTemplate.convertAndSend("/sub/find-game/" + uid1, typeMessageDTO);
        simpMessagingTemplate.convertAndSend("/sub/find-game/" + uid2, typeMessageDTO);
        
        // 플레이어 객체 생성
        Player player1 = Player.builder()
                .uid(uid1)
                .build();

        Player player2 = Player.builder()
                .uid(uid2)
                .build();

        // 게임 룸 생성
        GameRoom gameRoom = GameRoom.builder()
                .playerWhite(player1)
                .playerBlack(player2)
                .timeToStartGame(findGameRequest.getTimeLeft())
                .timeToAddEveryTurnStart(findGameRequest.getTimeToAddEveryTurnStart())
                .simpMessagingTemplate(simpMessagingTemplate)
                .gameRoomRepository(gameRoomRepository)
                .build();

        // 게임 룸 저장
        gameRoomRepository.addGameRoom(gameRoom);

        // 게임 시작
        gameRoom.startGame();

        return uid1;
    }
}
