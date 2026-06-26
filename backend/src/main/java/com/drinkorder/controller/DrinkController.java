package com.drinkorder.controller;

import com.drinkorder.entity.Drink;
import com.drinkorder.service.DrinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/drinks")
public class DrinkController {
    private final DrinkService drinkService;

    public DrinkController(DrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping
    public List<Drink> getDrinks(Long merchantId) {
        if (merchantId != null) {
            return drinkService.findByMerchantId(merchantId);
        } else {
            return drinkService.findAll();
        }
    }
}
