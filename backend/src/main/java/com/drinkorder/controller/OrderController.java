package com.drinkorder.controller;

import com.drinkorder.entity.Order;
import com.drinkorder.entity.OrderItem;
import com.drinkorder.service.OrderService;
import com.drinkorder.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.save(order);

        if (savedOrder.getCustomerId() != null && savedOrder.getTotal() != null) {
            String favoriteCategory = null;
            if (savedOrder.getItems() != null && !savedOrder.getItems().isEmpty()) {
                favoriteCategory = savedOrder.getItems().get(0).getName();
            }
            try {
                userService.updateConsumption(savedOrder.getCustomerId(), savedOrder.getTotal(), favoriteCategory);
            } catch (Exception e) {
                System.err.println("更新用户消费失败: " + e.getMessage());
            }
        }

        return savedOrder;
    }

    @GetMapping
    public List<Order> getOrders(@RequestParam(required = false) Long customerId, @RequestParam(required = false) Long branchId) {
        if (customerId != null) {
            return orderService.findByCustomerId(customerId);
        } else if (branchId != null) {
            return orderService.findByBranchId(branchId);
        }
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.findById(id);
    }
}