package com.drinkorder.mapper;

import com.drinkorder.entity.Drink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DrinkMapper {
    Drink findById(Long id);
    List<Drink> findAll();
    List<Drink> findByCategory(@Param("category") String category);
    List<Drink> findByMerchantId(@Param("merchantId") Long merchantId);
    List<Drink> findAvailable();
    void insert(Drink drink);
    void update(Drink drink);
    void delete(Long id);
    void updateStock(@Param("id") Long id, @Param("stock") Integer stock);
    void updateAvailable(@Param("id") Long id, @Param("available") Boolean available);
}
