package com.chessprojectspring.repository;

import com.chessprojectspring.game.GameRoom;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

@Repository
public class GameRoomRepository {

    private final Map<Long, GameRoom> gameRoomMap = new ConcurrentHashMap<>();
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
        gameRoomMap.remove(id);
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
