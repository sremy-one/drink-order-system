package com.drinkorder.entity;

import lombok.Data;

@Data
public class PointsProduct {
    private Long id;
    private String name;
    private String description;
    private Integer pointsRequired;
    private Integer stock;
    private String image;
}
