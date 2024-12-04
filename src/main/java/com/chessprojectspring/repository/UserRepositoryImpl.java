package com.chessprojectspring.repository;

import com.chessprojectspring.model.User;
import org.springframework.stereotype.Repository;

@Repository
public abstract class UserRepositoryImpl implements UserRepository {

    /**
     * 필요없는 값을 비워 리턴. getOpponent
     * @param uid
     * @return User Info for Opponent
     */
    @Override
    public User getOpponent(Long uid) {
        User user = findById(uid).orElse(null);
        if (user == null) {
            return null;
        }

        user.setUid(null); user.setUserName(null); user.setPassword(null);
        user.getRecord().setId(null); user.getRecord().setUser(null);

        return user;
    }
}
