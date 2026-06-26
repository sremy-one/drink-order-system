package com.drinkorder.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Drink {
    private Long id;

    @NotBlank(message = "饮品名称不能为空")
    private String name;

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须为正数")
    private Double price;

    private String category;
    private String image;
    private String description;
    private Integer stock;
    private Boolean available;
    private Long merchantId;
}
