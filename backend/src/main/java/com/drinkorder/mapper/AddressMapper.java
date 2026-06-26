package com.drinkorder.mapper;

import com.drinkorder.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {
    @Select("SELECT * FROM addresses WHERE user_id = #{userId}")
    List<Address> findByUserId(Long userId);

    @Select("SELECT * FROM addresses WHERE id = #{id}")
    Address findById(Long id);

    @Insert("INSERT INTO addresses (user_id, name, phone, address, is_default) VALUES (#{userId}, #{name}, #{phone}, #{address}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Address address);

    @Update("UPDATE addresses SET name = #{name}, phone = #{phone}, address = #{address}, is_default = #{isDefault} WHERE id = #{id}")
    void update(Address address);

    @Delete("DELETE FROM addresses WHERE id = #{id}")
    void delete(Long id);
}
