package com.drinkorder.mapper;

import com.drinkorder.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    Order findById(Long id);

    Order findByOrderId(@Param("orderId") String orderId);

    List<Order> findAll();

    List<Order> findByCustomerId(@Param("customerId") Long customerId);

    List<Order> findByBranchId(@Param("branchId") Long branchId);

    List<Order> findByStatus(@Param("status") String status);

    List<Order> findByCustomerIdAndType(@Param("customerId") Long customerId, @Param("type") String type);

    List<Order> findTodayOrdersByBranchId(@Param("branchId") Long branchId);

    void insert(Order order);

    void update(Order order);

    void delete(Long id);

    void updateStatus(@Param("id") Long id, @Param("status") String status);
}
