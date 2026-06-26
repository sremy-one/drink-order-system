package com.drinkorder.controller;

import com.drinkorder.entity.Coupon;
import com.drinkorder.service.CouponService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class PublicCouponController {
    private final CouponService couponService;

    public PublicCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/available")
    public List<Coupon> getAvailableCoupons() {
        return couponService.findAll();
    }
}
