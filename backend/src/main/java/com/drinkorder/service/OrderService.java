package com.drinkorder.service;

import com.drinkorder.entity.Order;
import com.drinkorder.entity.OrderItem;
import com.drinkorder.entity.Drink;
import com.drinkorder.entity.User;
import com.drinkorder.mapper.OrderMapper;
import com.drinkorder.mapper.OrderItemMapper;
import com.drinkorder.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final DrinkService drinkService;
    private final UserMapper userMapper;

    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper, 
                       DrinkService drinkService, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.drinkService = drinkService;
        this.userMapper = userMapper;
    }

    @Transactional
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setOrderId(generateOrderId());
            order.setPickupCode(generatePickupCode());
            order.setTime(java.time.LocalDateTime.now().toString());
            order.setStatus("制作中");
            orderMapper.insert(order);
            
            if (order.getPaymentMethod() != null && "balance".equals(order.getPaymentMethod()) && order.getCustomerId() != null) {
                User user = userMapper.findById(order.getCustomerId());
                if (user != null && user.getBalance() != null && user.getBalance() >= order.getTotal()) {
                    user.setBalance(user.getBalance() - order.getTotal());
                    userMapper.updateBalance(order.getCustomerId(), user.getBalance());
                }
            }

            if (order.getItems() != null && !order.getItems().isEmpty()) {
                saveOrderItems(order.getId(), order.getItems());
            }

            return orderMapper.findById(order.getId());
        } else {
            orderMapper.update(order);
            return orderMapper.findById(order.getId());
        }
    }

    @Transactional
    public void saveOrderItems(Long orderId, List<OrderItem> items) {
        if (items != null) {
            for (OrderItem item : items) {
                item.setOrderId(orderId);
                Drink drink = drinkService.findById(item.getDrinkId());
                if (drink != null) {
                    item.setImage(drink.getImage());
                    
                    int currentStock = drink.getStock() != null ? drink.getStock() : 0;
                    int newStock = currentStock - item.getQuantity();
                    drink.setStock(newStock);
                    drink.setAvailable(newStock > 0);
                    drinkService.save(drink);
                }
                orderItemMapper.insert(item);
            }
        }
    }

    public List<Order> findByCustomerId(Long customerId) {
        return orderMapper.findByCustomerId(customerId);
    }

    public List<Order> findAll() {
        return orderMapper.findAll();
    }

    public Order findById(Long id) {
        return orderMapper.findById(id);
    }

    public List<Order> findByBranchId(Long branchId) {
        return orderMapper.findByBranchId(branchId);
    }

    private String generateOrderId() {
        List<Order> activeOrders = orderMapper.findAll().stream()
                .filter(order -> !"已完成".equals(order.getStatus()) && !"已取消".equals(order.getStatus()))
                .toList();

        List<Integer> orderNumbers = activeOrders.stream()
                .map(order -> {
                    try {
                        String orderId = order.getOrderId();
                        if (orderId != null && orderId.startsWith("ORD")) {
                            String numStr = orderId.substring(3);
                            return Integer.parseInt(numStr);
                        }
                    } catch (Exception e) {
                    }
                    return 0;
                })
                .filter(num -> num > 0)
                .toList();

        int nextOrderNumber = 1;
        while (orderNumbers.contains(nextOrderNumber)) {
            nextOrderNumber++;
            if (nextOrderNumber > 999) {
                nextOrderNumber = 1;
            }
        }

        return "ORD" + nextOrderNumber;
    }

    private String generatePickupCode() {
        return String.valueOf(1000 + new Random().nextInt(9000));
    }
}
