package com.drinkorder.controller;

import com.drinkorder.entity.User;
import com.drinkorder.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/recharge")
    public ResponseEntity<?> recharge(@RequestBody Map<String, Double> request) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "未授权：请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email);

            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // 获取充值金额
            Double amount = request.get("amount");
            if (amount == null || amount <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "充值金额无效");
                return ResponseEntity.badRequest().body(error);
            }

            // 更新用户余额
            User updatedUser = userService.updateBalance(user.getId(), amount);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "充值失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("name") String name,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "未授权：请重新登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String email = authentication.getName();
            String role = authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .map(authority -> authority.substring(5))
                    .findFirst()
                    .orElse("user");
            User user = userService.findByEmailAndRole(email, role);

            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // 更新用户名
            user.setName(name);

            // 处理头像上传（已修复路径问题）
            if (avatar != null && !avatar.isEmpty()) {
                try {
                    // 项目根目录下的 uploads 文件夹（绝对路径，彻底解决找不到路径问题）
                    String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
                    File dir = new File(uploadDir);

                    // 自动创建多级目录
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    // 生成唯一文件名
                    String originalFilename = avatar.getOriginalFilename();
                    String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
                    File destFile = new File(dir, fileName);

                    // 保存文件
                    avatar.transferTo(destFile);

                    // 设置头像访问路径
                    user.setAvatar("/uploads/" + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    // 上传失败，保留原有头像
                }
            }

            // 保存更新
            User updatedUser = userService.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "更新失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}