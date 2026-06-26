package com.drinkorder.mapper;

import com.drinkorder.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    Review findById(Long id);
    List<Review> findByOrderId(@Param("orderId") Long orderId);
    List<Review> findByUserId(@Param("userId") Long userId);
    List<Review> findAll();
    void insert(Review review);
    void update(Review review);
    void delete(Long id);
    void updateReply(@Param("id") Long id, @Param("merchantReply") String merchantReply);
}
