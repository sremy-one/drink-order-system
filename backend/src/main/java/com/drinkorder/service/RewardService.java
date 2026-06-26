package com.drinkorder.service;

import com.drinkorder.entity.Reward;
import com.drinkorder.entity.User;
import com.drinkorder.mapper.RewardMapper;
import com.drinkorder.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RewardService {
    private final RewardMapper rewardMapper;
    private final UserMapper userMapper;

    public RewardService(RewardMapper rewardMapper, UserMapper userMapper) {
        this.rewardMapper = rewardMapper;
        this.userMapper = userMapper;
    }

    public List<Reward> findAll() {
        return rewardMapper.findAll();
    }

    public Reward findById(Long id) {
        return rewardMapper.findById(id);
    }

    @Transactional
    public boolean exchangeReward(Long rewardId, Long userId) {
        Reward reward = rewardMapper.findById(rewardId);
        User user = userMapper.findById(userId);

        if (reward == null || user == null || reward.getStock() <= 0 || user.getPoints() < reward.getPoints()) {
            return false;
        }

        reward.setStock(reward.getStock() - 1);
        rewardMapper.updateStock(rewardId, reward.getStock());

        user.setPoints(user.getPoints() - reward.getPoints());
        userMapper.update(user);

        return true;
    }
}
