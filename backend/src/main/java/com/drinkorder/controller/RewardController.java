package com.drinkorder.controller;

import com.drinkorder.entity.Reward;
import com.drinkorder.service.RewardService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping
    public List<Reward> getRewards() {
        return rewardService.findAll();
    }

    @PostMapping("/{id}/exchange")
    public boolean exchangeReward(@PathVariable Long id, @RequestParam Long userId) {
        return rewardService.exchangeReward(id, userId);
    }
}