package com.drinkorder.entity;

import lombok.Data;

@Data
public class Activity {
    private Long id;
    private String title;
    private String badge;
    private String description;
    private String tag;
    private String image;
    private boolean available;
}
