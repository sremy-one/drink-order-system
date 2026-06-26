package com.drinkorder.config;

import com.drinkorder.entity.Branch;
import com.drinkorder.entity.User;
import com.drinkorder.mapper.BranchMapper;
import com.drinkorder.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final BranchMapper branchMapper;
    private final UserMapper userMapper;

    public DataInitializer(BranchMapper branchMapper, UserMapper userMapper) {
        this.branchMapper = branchMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有分店数据
        if (branchMapper.findAll().isEmpty()) {
            // 创建默认商家
            if (userMapper.findByRole("merchant").isEmpty()) {
                User merchant = new User();
                merchant.setName("饮品店总商家");
                merchant.setEmail("merchant@example.com");
                merchant.setPassword("123456");
                merchant.setRole("merchant");
                merchant.setBalance(0.0);
                merchant.setIncome(0.0);
                userMapper.insert(merchant);
            }

            // 创建分店1
            Branch branch1 = new Branch();
            branch1.setName("中心店");
            branch1.setAddress("北京市朝阳区中心大街123号");
            branch1.setPhone("13800138001");
            branch1.setDistance(0.5);
            branch1.setRating(4.8);
            branch1.setOpen(true);
            branch1.setMerchantId(1L);
            branchMapper.insert(branch1);

            // 创建分店2
            Branch branch2 = new Branch();
            branch2.setName("望京店");
            branch2.setAddress("北京市朝阳区望京SOHO T1C座");
            branch2.setPhone("13800138002");
            branch2.setDistance(2.5);
            branch2.setRating(4.5);
            branch2.setOpen(true);
            branch2.setMerchantId(1L);
            branchMapper.insert(branch2);

            System.out.println("初始分店数据已添加");
        }
    }
}