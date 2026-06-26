package com.drinkorder.controller;

import com.drinkorder.entity.Address;
import com.drinkorder.entity.User;
import com.drinkorder.service.AddressService;
import com.drinkorder.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public ResponseEntity<?> getAddresses() {
        try {
            User user = getCurrentUser();
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "未授权：请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            List<Address> addresses = addressService.findByUserId(user.getId());
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "获取地址失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAddress(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "未授权：请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Address address = addressService.findById(id);
            if (address == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "地址不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // 验证地址属于当前用户
            if (!address.getUserId().equals(user.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "无权访问此地址");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            return ResponseEntity.ok(address);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "获取地址失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody Address address) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "未授权：请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // 设置用户ID
            address.setUserId(user.getId());
            address.setId(null); // 确保是新建

            // 如果设置为默认地址，取消其他默认地址（可选，根据需求）
            if (address.getIsDefault() == null) {
                address.setIsDefault(false);
            }

            Address savedAddress = addressService.save(address);
            return ResponseEntity.ok(savedAddress);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "添加地址失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "未授权：请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // 检查地址是否存在且属于当前用户
            Address existingAddress = addressService.findById(id);
            if (existingAddress == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "地址不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            if (!existingAddress.getUserId().equals(user.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "无权修改此地址");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // 更新字段
            existingAddress.setName(address.getName());
            existingAddress.setPhone(address.getPhone());
            existingAddress.setAddress(address.getAddress());
            existingAddress.setIsDefault(address.getIsDefault() != null ? address.getIsDefault() : false);

            Address updatedAddress = addressService.save(existingAddress);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "更新地址失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "未授权：请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // 检查地址是否存在且属于当前用户
            Address address = addressService.findById(id);
            if (address == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "地址不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            if (!address.getUserId().equals(user.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "无权删除此地址");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            addressService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "删除地址失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
