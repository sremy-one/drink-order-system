package com.drinkorder.controller;

import com.drinkorder.entity.User;
import com.drinkorder.service.UserService;
import com.drinkorder.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        String role = loginData.get("role");

        if (email == null || email.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "账号不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        if (password == null || password.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        if (role == null || role.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "角色不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.findByEmailAndRole(email, role);
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "用户不存在或角色不匹配");
            return ResponseEntity.status(401).body(response);
        }

        if (!userService.checkPassword(password, user.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "密码错误");
            return ResponseEntity.status(401).body(response);
        }

        String token = JwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "账号不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "角色不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            User newUser = userService.register(user);
            String token = JwtUtil.generateToken(newUser.getId(), newUser.getEmail(), newUser.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", newUser);
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "该账号已被注册，请使用其他账号");
            return ResponseEntity.status(409).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "注册失败，请稍后重试");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "授权头缺失或无效"));
        }

        String token = authorization.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtil.validateToken(token);
        if (claims == null) {
            return ResponseEntity.status(401).body(Map.of("message", "令牌无效或已过期"));
        }
        String email = (String) claims.get("email");
        String role = (String) claims.get("role");
        return ResponseEntity.ok(userService.findByEmailAndRole(email, role));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();

        if (errors.containsKey("password")) {
            response.put("message", errors.get("password"));
        } else if (errors.containsKey("email")) {
            response.put("message", errors.get("email"));
        } else if (errors.containsKey("name")) {
            response.put("message", errors.get("name"));
        } else {
            response.put("message", "注册信息无效");
            response.put("errors", errors);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        if (ex instanceof DataIntegrityViolationException) {
            response.put("message", "该账号已被注册，请使用其他账号");
        } else {
            response.put("message", "服务器错误，请稍后重试");
        }
        return ResponseEntity.badRequest().body(response);
    }
}