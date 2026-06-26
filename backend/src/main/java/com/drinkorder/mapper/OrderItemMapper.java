package com.drinkorder.mapper;

import com.drinkorder.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    OrderItem findById(Long id);
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    void insert(OrderItem item);
    void update(OrderItem item);
    void delete(Long id);
    void deleteByOrderId(@Param("orderId") Long orderId);
}
