package com.drinkorder.mapper;

import com.drinkorder.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActivityMapper {
    Activity findById(Long id);
    List<Activity> findAll();
    List<Activity> findAvailable();
    void insert(Activity activity);
    void update(Activity activity);
    void delete(Long id);
}
