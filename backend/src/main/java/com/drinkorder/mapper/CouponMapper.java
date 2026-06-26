package com.drinkorder.mapper;

import com.drinkorder.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {
    Coupon findById(Long id);
    Coupon findByCode(@Param("code") String code);
    List<Coupon> findAll();
    List<Coupon> findActive();
    List<Coupon> findByMemberLevel(@Param("memberLevel") String memberLevel);
    void insert(Coupon coupon);
    void update(Coupon coupon);
    void delete(Long id);
    void updateActive(@Param("id") Long id, @Param("isActive") Boolean isActive);
}
