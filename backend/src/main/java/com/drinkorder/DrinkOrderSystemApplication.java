package com.drinkorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.drinkorder.mapper")
public class DrinkOrderSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrinkOrderSystemApplication.class, args);
    }
}