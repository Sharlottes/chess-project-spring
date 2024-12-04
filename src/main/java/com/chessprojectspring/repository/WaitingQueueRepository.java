package com.chessprojectspring.repository;

import com.chessprojectspring.game.GameRoom;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
public class WaitingQueueRepository {

    private final ConcurrentLinkedQueue<Long> waitingQueue = new ConcurrentLinkedQueue<>(); // 스레드 안전한 큐
    private final Object queueLock = new Object();

    public synchronized void startGame(GameRoom gameRoom) {
        // 게임 시작 로직
    }

    /**
     * 게임 대기 큐에 플레이어 추가
     * @param uid 플레이어의 UID
     */
    public void addToWaitingQueue(Long uid) {
        synchronized (queueLock) {
            waitingQueue.add(uid);
        }
    }

    /**
     * 대기 큐에 있는 플레이어 수 반환
     * @return 대기 큐에 있는 플레이어 수
     */
    public int getWaitingQueueSize() {
        return waitingQueue.size();
    }

    /**
     * 대기 큐에 있는 플레이어 중 한명을 꺼내어 반환
     * @return 플레이어의 UID, 대기 큐에 없으면 null 반환
     */
    public Long pollOneFromQueue() {
        return waitingQueue.poll();
    }

    public void removeFromWaitingQueue(Long uid) {
        waitingQueue.remove(uid);
    }

//    /**
//     * 대기 큐에서 플레이어 두 명을 꺼내어 반환
//     * @return 두 명의 UID가 담긴 배열, 충분하지 않으면 null 반환
//     */
//    public Long[] pollTwoFromQueue() {
//        synchronized (queueLock) {
//            if (waitingQueue.size() >= 2) {
//                Long first = waitingQueue.poll();
//                Long second = waitingQueue.poll();
//                if (first != null && second != null) {
//                    return new Long[]{first, second};
//                }
//            }
//            return null;
//        }
//    }
} 