package com.drinkorder.service;

import com.drinkorder.entity.Activity;
import com.drinkorder.mapper.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityMapper activityMapper;

    public ActivityService(ActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }

    public List<Activity> findAll() {
        return activityMapper.findAll();
    }

    public Activity findById(Long id) {
        return activityMapper.findById(id);
    }

    @Transactional
    public Activity save(Activity activity) {
        if (activity.getId() == null) {
            activityMapper.insert(activity);
            return activityMapper.findById(activity.getId());
        } else {
            activityMapper.update(activity);
            return activityMapper.findById(activity.getId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        activityMapper.delete(id);
    }
}
