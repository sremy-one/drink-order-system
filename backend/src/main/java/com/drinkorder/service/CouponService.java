package com.drinkorder.service;

import com.drinkorder.entity.Coupon;
import com.drinkorder.entity.User;
import com.drinkorder.entity.UserCoupon;
import com.drinkorder.mapper.CouponMapper;
import com.drinkorder.mapper.UserCouponMapper;
import com.drinkorder.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponService {
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final UserMapper userMapper;

    public CouponService(CouponMapper couponMapper, UserCouponMapper userCouponMapper, UserMapper userMapper) {
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
        this.userMapper = userMapper;
    }

    public List<Coupon> findAll() {
        return couponMapper.findAll();
    }

    public Coupon findById(Long id) {
        return couponMapper.findById(id);
    }

    @Transactional
    public Coupon save(Coupon coupon) {
        if (coupon.getIsActive() == null) {
            coupon.setIsActive(true);
        }
        if (coupon.getId() == null) {
            couponMapper.insert(coupon);
            return couponMapper.findById(coupon.getId());
        } else {
            couponMapper.update(coupon);
            return couponMapper.findById(coupon.getId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        couponMapper.delete(id);
    }

    @Transactional
    public List<User> issueCouponToQualifiedUsers(Long couponId) {
        Coupon coupon = findById(couponId);
        if (coupon == null || !coupon.getIsActive()) {
            throw new RuntimeException("优惠券不存在或已失效");
        }

        List<User> qualifiedUsers = new ArrayList<>();

        if (coupon.getTargetMemberLevel() != null && !coupon.getTargetMemberLevel().isEmpty()) {
            qualifiedUsers.addAll(userMapper.findByMemberLevel(coupon.getTargetMemberLevel()));
        }

        if (coupon.getMinConsumption() != null && coupon.getMinConsumption() > 0) {
            List<User> consumptionQualifiedUsers = userMapper
                    .findByTotalConsumptionGreaterThanEqual(coupon.getMinConsumption());
            for (User user : consumptionQualifiedUsers) {
                if (!qualifiedUsers.contains(user)) {
                    qualifiedUsers.add(user);
                }
            }
        }

        if (qualifiedUsers.isEmpty()) {
            qualifiedUsers = userMapper.findByRole("customer");
        }

        return qualifiedUsers;
    }

    public List<UserCoupon> findUserCoupons(Long userId) {
        return userCouponMapper.findByUserId(userId);
    }

    public List<Coupon> findAvailableCouponsForUser(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        List<Coupon> allCoupons = findAll();
        List<UserCoupon> userCoupons = findUserCoupons(userId);

        List<Coupon> availableCoupons = new ArrayList<>();
        for (Coupon coupon : allCoupons) {
            if (coupon.getIsActive() == null || !coupon.getIsActive())
                continue;

            boolean alreadyIssued = userCoupons.stream()
                    .anyMatch(uc -> uc.getCouponId().equals(coupon.getId()));
            if (alreadyIssued)
                continue;

            if (coupon.getTargetMemberLevel() != null && !coupon.getTargetMemberLevel().isEmpty()) {
                if (!coupon.getTargetMemberLevel().equals(user.getMemberLevel())) {
                    continue;
                }
            }

            if (coupon.getMinConsumption() != null && coupon.getMinConsumption() > 0) {
                if (user.getTotalConsumption() == null || user.getTotalConsumption() < coupon.getMinConsumption()) {
                    continue;
                }
            }

            availableCoupons.add(coupon);
        }

        return availableCoupons;
    }

    @Transactional
    public boolean claimCoupon(Long userId, Long couponId) {
        List<UserCoupon> userCoupons = userCouponMapper.findByUserId(userId);
        UserCoupon existingCoupon = userCoupons.stream()
                .filter(uc -> uc.getCouponId().equals(couponId))
                .findFirst()
                .orElse(null);

        if (existingCoupon != null) {
            return false;
        }

        Coupon coupon = findById(couponId);
        if (coupon == null || coupon.getIsActive() == null || !coupon.getIsActive()) {
            return false;
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }

        if (coupon.getTargetMemberLevel() != null && !coupon.getTargetMemberLevel().isEmpty()) {
            if (!coupon.getTargetMemberLevel().equals(user.getMemberLevel())) {
                return false;
            }
        }

        if (coupon.getMinConsumption() != null && coupon.getMinConsumption() > 0) {
            if (user.getTotalConsumption() == null || user.getTotalConsumption() < coupon.getMinConsumption()) {
                return false;
            }
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setIsUsed(false);
        userCoupon.setIssuedAt(java.time.LocalDateTime.now().toString());
        userCouponMapper.insert(userCoupon);

        return true;
    }
}
