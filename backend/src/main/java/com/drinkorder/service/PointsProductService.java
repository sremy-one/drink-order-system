package com.drinkorder.service;

import com.drinkorder.entity.PointsProduct;
import com.drinkorder.mapper.PointsProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointsProductService {
    private final PointsProductMapper pointsProductMapper;

    public PointsProductService(PointsProductMapper pointsProductMapper) {
        this.pointsProductMapper = pointsProductMapper;
    }

    public List<PointsProduct> findAll() {
        return pointsProductMapper.findAll();
    }

    public PointsProduct findById(Long id) {
        return pointsProductMapper.findById(id);
    }

    @Transactional
    public PointsProduct save(PointsProduct product) {
        if (product.getId() == null) {
            pointsProductMapper.insert(product);
            return pointsProductMapper.findById(product.getId());
        } else {
            pointsProductMapper.update(product);
            return pointsProductMapper.findById(product.getId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        pointsProductMapper.delete(id);
    }
}
