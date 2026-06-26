package com.drinkorder.service;

import com.drinkorder.entity.Review;
import com.drinkorder.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    public List<Review> findAll() {
        return reviewMapper.findAll();
    }

    public Review findById(Long id) {
        return reviewMapper.findById(id);
    }

    @Transactional
    public Review save(Review review) {
        if (review.getId() == null) {
            reviewMapper.insert(review);
            return reviewMapper.findById(review.getId());
        } else {
            reviewMapper.update(review);
            return reviewMapper.findById(review.getId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        reviewMapper.delete(id);
    }
}
