package com.drinkorder.controller;
import com.drinkorder.entity.Drink;
import com.drinkorder.service.DrinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/merchant/products")
public class MerchantProductController {
    private final DrinkService drinkService;

    public MerchantProductController(DrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping
    public List<Drink> getProducts(@RequestParam(required = false) Long merchantId) {
        if (merchantId != null) {
            return drinkService.findByMerchantId(merchantId);
        } else {
            return drinkService.findAll();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Drink> getProductById(@PathVariable Long id) {
        Drink drink = drinkService.findById(id);
        if (drink != null) {
            return ResponseEntity.ok(drink);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Drink createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam("category") String category,
            @RequestParam("available") String available,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        Drink drink = new Drink();
        drink.setName(name);
        drink.setDescription(description);
        drink.setPrice(price);
        drink.setStock(stock);
        drink.setCategory(category);
        drink.setAvailable(Boolean.parseBoolean(available));
        
        if (imageFile != null && !imageFile.isEmpty()) {
            drink.setImage(saveImage(imageFile));
        }
        
        return drinkService.save(drink);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Drink> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") Integer stock,
            @RequestParam("category") String category,
            @RequestParam("available") String available,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        Drink existing = drinkService.findById(id);
        if (existing != null) {
            existing.setName(name);
            existing.setDescription(description);
            existing.setPrice(price);
            existing.setStock(stock);
            existing.setCategory(category);
            existing.setAvailable(Boolean.parseBoolean(available));
            
            if (imageFile != null && !imageFile.isEmpty()) {
                existing.setImage(saveImage(imageFile));
            }
            
            return ResponseEntity.ok(drinkService.save(existing));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        drinkService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Drink> toggleProductStatus(@PathVariable Long id) {
        Drink drink = drinkService.findById(id);
        if (drink != null) {
            drink.setAvailable(!drink.getAvailable());
            return ResponseEntity.ok(drinkService.save(drink));
        }
        return ResponseEntity.notFound().build();
    }

    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            File destFile = new File(dir, fileName);
            file.transferTo(destFile);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
