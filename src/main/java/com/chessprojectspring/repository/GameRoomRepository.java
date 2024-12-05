package com.chessprojectspring.repository;

import com.chessprojectspring.game.GameRoom;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GameRoomRepository {

    private final ConcurrentHashMap<Long, GameRoom> gameRoomMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    // 게임룸 추가
    public Long addGameRoom(GameRoom gameRoom) {
        Long id = idGenerator.incrementAndGet();
        gameRoom.setId(id);
        gameRoomMap.put(id, gameRoom);
        return id;
    }

    // 게임룸 조회
    public GameRoom getGameRoom(Long id) {
        return gameRoomMap.get(id);
    }

    // 게임룸 삭제
    public void removeGameRoom(Long id) {
        log.debug("[GameRoomRepository-removeGameRoom] 게임 룸 삭제 시작: {}", id);
        try {
            gameRoomMap.remove(id);
        } catch (Exception e) {
            log.error("[GameRoomRepository-removeGameRoom] 게임 룸 삭제 중 예외 발생: {}", id, e.getMessage());
        }
        log.debug("[GameRoomRepository-removeGameRoom] 게임 룸 삭제 완료: {}", id);

        log.debug("--------------------------------");
        log.debug("게임룸 삭제: {}", id);
        log.debug("게임룸 사이즈: {}", gameRoomMap.size());
        log.debug("--------------------------------");
        log.debug("게임룸 목록: ");
        gameRoomMap.forEach((key, value) -> {
            log.debug("게임룸 ID: {}", key);
        });
    }

    // 모든 게임룸 조회
    public Map<Long, GameRoom> getAllGameRooms() {
        return new ConcurrentHashMap<>(gameRoomMap);
    }

    // uid 를 통해 게임룸 조회
    public GameRoom getGameRoomByUid(Long uid) {
        return gameRoomMap.values().stream()
                .filter(gameRoom -> gameRoom.getPlayerWhite().getUid().equals(uid) || gameRoom.getPlayerBlack().getUid().equals(uid))
                .findFirst()
                .orElse(null);
    }

    // 게임 중인 상태인지 검사
    public boolean isInGame(Long uid) {
        return gameRoomMap.values().stream()
                .anyMatch(gameRoom -> gameRoom.getPlayerWhite().getUid().equals(uid) || gameRoom.getPlayerBlack().getUid().equals(uid));
    }
}
