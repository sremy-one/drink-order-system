package com.drinkorder.entity;

import lombok.Data;

@Data
public class Reward {
    private Long id;
    private String name;
    private String description;
    private Integer points;
    private Integer stock;
}
