package com.drinkorder.mapper;

import com.drinkorder.entity.PointsProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointsProductMapper {
    PointsProduct findById(Long id);
    List<PointsProduct> findAll();
    void insert(PointsProduct product);
    void update(PointsProduct product);
    void delete(Long id);
    void updateStock(Long id, Integer stock);
}
