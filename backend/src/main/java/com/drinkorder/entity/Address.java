package com.drinkorder.entity;

import lombok.Data;

@Data
public class Address {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String address;
    private Boolean isDefault;
}
