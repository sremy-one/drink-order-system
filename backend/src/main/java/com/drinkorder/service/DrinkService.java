package com.drinkorder.service;

import com.drinkorder.entity.Drink;
import com.drinkorder.mapper.DrinkMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DrinkService {
    private final DrinkMapper drinkMapper;

    public DrinkService(DrinkMapper drinkMapper) {
        this.drinkMapper = drinkMapper;
    }

    public List<Drink> findAll() {
        return drinkMapper.findAll();
    }

    public Drink findById(Long id) {
        return drinkMapper.findById(id);
    }

    @Transactional
    public Drink save(Drink drink) {
        if (drink.getId() == null) {
            drinkMapper.insert(drink);
            return drinkMapper.findById(drink.getId());
        } else {
            drinkMapper.update(drink);
            return drinkMapper.findById(drink.getId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        drinkMapper.delete(id);
    }

    public List<Drink> findByMerchantId(Long merchantId) {
        return drinkMapper.findByMerchantId(merchantId);
    }
}
