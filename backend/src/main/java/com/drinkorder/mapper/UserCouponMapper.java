package com.drinkorder.mapper;

import com.drinkorder.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCouponMapper {
    UserCoupon findById(Long id);
    List<UserCoupon> findByUserId(@Param("userId") Long userId);
    List<UserCoupon> findByUserIdAndIsUsed(@Param("userId") Long userId, @Param("isUsed") Boolean isUsed);
    UserCoupon findByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);
    void insert(UserCoupon userCoupon);
    void update(UserCoupon userCoupon);
    void delete(Long id);
    void updateUsed(@Param("id") Long id, @Param("isUsed") Boolean isUsed, @Param("usedAt") String usedAt);
}
