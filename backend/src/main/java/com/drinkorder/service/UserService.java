package com.drinkorder.service;

import com.drinkorder.entity.User;
import com.drinkorder.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public static final String[] MEMBER_LEVELS = {"初级", "白银", "黄金", "白金", "超级", "至尊"};
    public static final double[] MEMBER_THRESHOLDS = {0.0, 100.0, 500.0, 1000.0, 5000.0, 10000.0};

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User findByEmailAndRole(String email, String role) {
        return userMapper.findByEmailAndRole(email, role);
    }

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Transactional
    public User register(User user) {
        User existingUser = userMapper.findByEmailAndRole(user.getEmail(), user.getRole());
        if (existingUser != null) {
            throw new RuntimeException("该账号已被注册");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getBalance() == null) {
            user.setBalance(0.0);
        }
        if (user.getMemberLevel() == null) {
            user.setMemberLevel("初级");
        }
        if (user.getTotalConsumption() == null) {
            user.setTotalConsumption(0.0);
        }
        if (user.getTotalDrinks() == null) {
            user.setTotalDrinks(0);
        }
        if (user.getIncome() == null) {
            user.setIncome(0.0);
        }
        userMapper.insert(user);
        return userMapper.findByEmail(user.getEmail());
    }

    @Transactional
    public User updateBalance(Long userId, Double amount) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (amount < 0) {
            throw new RuntimeException("充值金额不能为负数");
        }
        if (user.getBalance() == null) {
            user.setBalance(0.0);
        }
        user.setBalance(user.getBalance() + amount);
        userMapper.updateBalance(userId, user.getBalance());
        return userMapper.findById(userId);
    }

    @Transactional
    public User updateConsumption(Long userId, Double orderAmount, String drinkCategory) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getTotalConsumption() == null) {
            user.setTotalConsumption(0.0);
        }
        if (user.getTotalDrinks() == null) {
            user.setTotalDrinks(0);
        }
        if (user.getMemberLevel() == null) {
            user.setMemberLevel("初级");
        }

        user.setTotalConsumption(user.getTotalConsumption() + orderAmount);
        user.setTotalDrinks(user.getTotalDrinks() + 1);

        updateMemberLevel(user);

        if (drinkCategory != null && !drinkCategory.isEmpty()) {
            user.setFavoriteCategory(drinkCategory);
        }

        userMapper.updateConsumption(userId, user.getTotalConsumption(), user.getTotalDrinks(), 
                                      user.getMemberLevel(), user.getFavoriteCategory());
        return userMapper.findById(userId);
    }

    @Transactional
    public User refundBalance(Long userId, Double amount) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (amount < 0) {
            throw new RuntimeException("退款金额不能为负数");
        }
        if (user.getBalance() == null) {
            user.setBalance(0.0);
        }
        user.setBalance(user.getBalance() + amount);
        userMapper.updateBalance(userId, user.getBalance());
        return userMapper.findById(userId);
    }

    @Transactional
    public User addIncome(Long userId, Double amount) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (amount < 0) {
            throw new RuntimeException("收入金额不能为负数");
        }
        if (user.getIncome() == null) {
            user.setIncome(0.0);
        }
        user.setIncome(user.getIncome() + amount);
        userMapper.updateIncome(userId, user.getIncome());
        return userMapper.findById(userId);
    }

    private void updateMemberLevel(User user) {
        Double totalConsumption = user.getTotalConsumption();
        if (totalConsumption == null) {
            totalConsumption = 0.0;
        }

        String newLevel = "初级";
        for (int i = MEMBER_THRESHOLDS.length - 1; i >= 0; i--) {
            if (totalConsumption >= MEMBER_THRESHOLDS[i]) {
                newLevel = MEMBER_LEVELS[i];
                break;
            }
        }
        user.setMemberLevel(newLevel);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            userMapper.insert(user);
            return userMapper.findByEmail(user.getEmail());
        } else {
            userMapper.update(user);
            return userMapper.findById(user.getId());
        }
    }
}
