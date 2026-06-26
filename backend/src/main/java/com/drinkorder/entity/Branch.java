package com.drinkorder.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Branch {
    private Long id;

    @NotBlank(message = "分店名称不能为空")
    private String name;

    private String address;
    private String phone;
    private Double distance;
    private Double rating;
    private Boolean open;
    private Long merchantId;
}
