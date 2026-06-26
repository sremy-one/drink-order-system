package com.drinkorder.entity;

import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long drinkId;
    private String name;
    private Integer quantity;
    private Double price;
    private String image;
    private Order order;

    public Integer getCount() {
        return quantity;
    }

    public void setCount(Integer count) {
        this.quantity = count;
    }
}
