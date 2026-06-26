package com.drinkorder.controller;

import com.drinkorder.entity.Order;
import com.drinkorder.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/orders")
public class MerchantOrderController {
    private final OrderService orderService;

    public MerchantOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getOrders(@RequestParam(required = false) Long branchId) {
        if (branchId != null) {
            return orderService.findByBranchId(branchId);
        }
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        Order order = orderService.findById(id);
        if (order != null && status != null) {
            order.setStatus(status);
            return ResponseEntity.ok(orderService.save(order));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order != null) {
            order.setStatus("CANCELLED");
            return ResponseEntity.ok(orderService.save(order));
        }
        return ResponseEntity.notFound().build();
    }
}
