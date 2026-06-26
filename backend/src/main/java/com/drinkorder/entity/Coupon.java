package com.drinkorder.entity;

import lombok.Data;

@Data
public class Coupon {
    private Long id;
    private String code;
    private Double discount;
    private Double minimumSpend;
    private String expireDate;
    private String targetMemberLevel;
    private Double minConsumption;
    private Boolean isActive;
    private String description;
}
