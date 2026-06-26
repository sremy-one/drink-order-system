package com.drinkorder.entity;

import lombok.Data;

@Data
public class Review {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long orderId;
    private Integer rating;
    private String content;
    private String merchantReply;
    private String createdAt;
}
