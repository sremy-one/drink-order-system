package com.drinkorder.controller;

import com.drinkorder.entity.Branch;
import com.drinkorder.entity.Order;
import com.drinkorder.entity.User;
import com.drinkorder.mapper.UserMapper;
import com.drinkorder.service.BranchService;
import com.drinkorder.service.OrderService;
import com.drinkorder.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    private final UserMapper userMapper;
    private final OrderService orderService;
    private final UserService userService;
    private final BranchService branchService;

    public MerchantController(UserMapper userMapper, OrderService orderService, UserService userService,
            BranchService branchService) {
        this.userMapper = userMapper;
        this.orderService = orderService;
        this.userService = userService;
        this.branchService = branchService;
    }

    @GetMapping
    public List<User> getMerchants() {
        return userMapper.findByRole("merchant");
    }

    @GetMapping("/branches")
    public List<Branch> getBranches() {
        return branchService.findAll();
    }

    @PostMapping("/branches")
    public Branch addBranch(@RequestBody Branch branch) {
        return branchService.save(branch);
    }

    @PutMapping("/branches/{id}")
    public Branch updateBranch(@PathVariable Long id, @RequestBody Branch branch) {
        Branch existingBranch = branchService.findById(id);
        if (existingBranch != null) {
            branch.setId(id);
            return branchService.save(branch);
        }
        return null;
    }

    @DeleteMapping("/branches/{id}")
    public void deleteBranch(@PathVariable Long id) {
        branchService.deleteById(id);
    }

    @PatchMapping("/branches/{id}/toggle")
    public Branch toggleBranchStatus(@PathVariable Long id) {
        Branch branch = branchService.findById(id);
        if (branch != null) {
            branch.setOpen(!branch.getOpen());
            return branchService.save(branch);
        }
        return null;
    }

    @PatchMapping("/orders/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Order order = orderService.findById(id);
        if (order != null) {
            String newStatus = request.get("status");
            String currentStatus = order.getStatus();

            // 状态流转逻辑
            if ("制作中".equals(currentStatus) && "已出餐".equals(newStatus)) {
                // 商家将订单标记为已出餐
                order.setStatus(newStatus);
            } else if ("已出餐".equals(currentStatus) && "已取餐".equals(newStatus)) {
                // 客户标记为已取餐，自动变为已完成
                order.setStatus("已完成");
                // 订单完成后，商家收入增加
                addMerchantIncome(order);
            }

            return orderService.save(order);
        }
        return null;
    }

    @PatchMapping("/orders/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order != null) {
            order.setStatus("已取消");
            return orderService.save(order);
        }
        return null;
    }

    // 增加商家收入
    private void addMerchantIncome(Order order) {
        try {
            // 找到唯一的商家（所有分店共用一个商家）
            List<User> merchants = userMapper.findByRole("merchant");
            if (merchants != null && !merchants.isEmpty()) {
                User merchant = merchants.get(0);
                if (order.getTotal() != null) {
                    userService.addIncome(merchant.getId(), order.getTotal());
                }
            }
        } catch (Exception e) {
            System.err.println("增加商家收入失败: " + e.getMessage());
        }
    }
}