package com.drinkorder.entity;

import lombok.Data;
import java.util.List;

@Data
public class Order {
    private Long id;
    private String orderId;
    private String type;
    private String status;
    private Double total;
    private String time;
    private Long customerId;
    private Long branchId;
    private String pickupCode;
    private String paymentMethod;
    private List<OrderItem> items;
}
