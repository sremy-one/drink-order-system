package com.drinkorder.mapper;

import com.drinkorder.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long id);
    User findByEmail(@Param("email") String email);
    User findByEmailAndRole(@Param("email") String email, @Param("role") String role);
    List<User> findByRole(@Param("role") String role);
    List<User> findByMemberLevel(@Param("memberLevel") String memberLevel);
    List<User> findByTotalConsumptionGreaterThanEqual(@Param("minConsumption") Double minConsumption);
    void insert(User user);
    void update(User user);
    void delete(Long id);
    void updateBalance(@Param("id") Long id, @Param("balance") Double balance);
    void updateConsumption(@Param("id") Long id, @Param("totalConsumption") Double totalConsumption, 
                           @Param("totalDrinks") Integer totalDrinks, @Param("memberLevel") String memberLevel,
                           @Param("favoriteCategory") String favoriteCategory);
    void updateIncome(@Param("id") Long id, @Param("income") Double income);
}
