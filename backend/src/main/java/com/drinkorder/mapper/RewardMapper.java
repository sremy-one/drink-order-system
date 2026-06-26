package com.drinkorder.mapper;

import com.drinkorder.entity.Reward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RewardMapper {
    Reward findById(Long id);
    List<Reward> findAll();
    void insert(Reward reward);
    void update(Reward reward);
    void delete(Long id);
    void updateStock(Long id, Integer stock);
}
