package com.drinkorder.entity;

import lombok.Data;

@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private Boolean isUsed;
    private String issuedAt;
    private String usedAt;
}
