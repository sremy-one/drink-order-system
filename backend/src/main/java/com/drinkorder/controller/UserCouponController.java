package com.drinkorder.controller;

import com.drinkorder.entity.Coupon;
import com.drinkorder.entity.UserCoupon;
import com.drinkorder.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/coupons")
public class UserCouponController {
    private final CouponService couponService;

    public UserCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/available")
    public List<Coupon> getAvailableCoupons(@RequestParam Long userId) {
        return couponService.findAvailableCouponsForUser(userId);
    }

    @GetMapping("/my")
    public List<Map<String, Object>> getMyCoupons(@RequestParam Long userId) {
        List<UserCoupon> userCoupons = couponService.findUserCoupons(userId);
        return userCoupons.stream().map(uc -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", uc.getId());
            map.put("userId", uc.getUserId());
            map.put("couponId", uc.getCouponId());
            map.put("isUsed", uc.getIsUsed());
            map.put("issuedAt", uc.getIssuedAt());
            map.put("usedAt", uc.getUsedAt());
            Coupon coupon = couponService.findById(uc.getCouponId());
            if (coupon != null) {
                map.put("code", coupon.getCode());
                map.put("discount", coupon.getDiscount());
                map.put("minimumSpend", coupon.getMinimumSpend());
                map.put("expireDate", coupon.getExpireDate());
                map.put("description", coupon.getDescription());
            }
            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping("/claim")
    public ResponseEntity<?> claimCoupon(@RequestParam Long userId, @RequestParam Long couponId) {
        try {
            boolean success = couponService.claimCoupon(userId, couponId);
            if (success) {
                return ResponseEntity.ok().body(Map.of("message", "领券成功"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "领券失败，该券不可领取或已领取"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "领券失败: " + e.getMessage()));
        }
    }
}