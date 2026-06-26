package com.drinkorder.controller;

import com.drinkorder.entity.PointsProduct;
import com.drinkorder.service.PointsProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/merchant/points-products")
public class PointsProductController {
    private final PointsProductService pointsProductService;

    // 统一上传目录（和项目其他上传保持一致）
    private final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

    public PointsProductController(PointsProductService pointsProductService) {
        this.pointsProductService = pointsProductService;
        // 启动时自动创建目录
        createUploadFolderIfNotExists();
    }

    // 统一创建目录（只执行一次）
    private void createUploadFolderIfNotExists() {
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @GetMapping
    public List<PointsProduct> getAllProducts() {
        return pointsProductService.findAll();
    }

    @GetMapping("/{id}")
    public PointsProduct getProductById(@PathVariable Long id) {
        return pointsProductService.findById(id);
    }

    @PostMapping
    public PointsProduct createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("pointsRequired") String pointsRequired,
            @RequestParam("stock") String stock,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        PointsProduct product = new PointsProduct();
        product.setName(name);
        product.setDescription(description);
        product.setPointsRequired(
                pointsRequired != null && !pointsRequired.isEmpty() ? Integer.parseInt(pointsRequired) : 0);
        product.setStock(stock != null && !stock.isEmpty() ? Integer.parseInt(stock) : 0);

        if (image != null && !image.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File destFile = new File(uploadDir, fileName);

                image.transferTo(destFile);
                product.setImage("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                product.setImage(
                        "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=gift%20box%20cartoon&image_size=square");
            }
        } else {
            product.setImage(
                    "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=gift%20box%20cartoon&image_size=square");
        }

        return pointsProductService.save(product);
    }

    @PutMapping("/{id}")
    public PointsProduct updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("pointsRequired") String pointsRequired,
            @RequestParam("stock") String stock,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        PointsProduct product = pointsProductService.findById(id);
        if (product == null) {
            return null;
        }

        product.setName(name);
        product.setDescription(description);
        product.setPointsRequired(
                pointsRequired != null && !pointsRequired.isEmpty() ? Integer.parseInt(pointsRequired) : 0);
        product.setStock(stock != null && !stock.isEmpty() ? Integer.parseInt(stock) : 0);

        if (image != null && !image.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File destFile = new File(uploadDir, fileName);

                image.transferTo(destFile);
                product.setImage("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pointsProductService.save(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        pointsProductService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/exchange")
    public ResponseEntity<?> exchangeProduct(@PathVariable Long id, @RequestParam Long userId) {
        try {
            PointsProduct product = pointsProductService.findById(id);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            if (product.getStock() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "商品库存不足"));
            }
            // 这里可以添加积分扣除和库存减少的逻辑
            product.setStock(product.getStock() - 1);
            pointsProductService.save(product);
            return ResponseEntity.ok().body(Map.of("message", "兑换成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "兑换失败: " + e.getMessage()));
        }
    }
}