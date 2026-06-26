package com.drinkorder.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class User {
    private Long id;

    @NotBlank(message = "账号不能为空")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$",
             message = "密码至少8个字符，需包含大写字母、小写字母和数字")
    private String password;

    @NotBlank(message = "角色不能为空")
    private String role;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String avatar;
    private Double balance;
    private Integer coupons;
    private Integer points;
    private String memberLevel;
    private Double totalConsumption;
    private Integer totalDrinks;
    private String favoriteCategory;
    private String shopName;
    private String address;
    private String phone;
    private Double income;
}
