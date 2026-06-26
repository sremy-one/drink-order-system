package com.drinkorder.controller;

import com.drinkorder.entity.Activity;
import com.drinkorder.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/merchant/activities")
public class MerchantActivityController {
    private final ActivityService activityService;

    public MerchantActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<Activity> getActivities() {
        return activityService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.findById(id);
        if (activity != null) {
            return ResponseEntity.ok(activity);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Activity createActivity(
            @RequestParam("title") String title,
            @RequestParam("badge") String badge,
            @RequestParam("description") String description,
            @RequestParam("available") String available,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        Activity activity = new Activity();
        activity.setTitle(title);
        activity.setBadge(badge);
        activity.setTag(badge);
        activity.setDescription(description);
        activity.setAvailable(Boolean.parseBoolean(available));

        if (imageFile != null && !imageFile.isEmpty()) {
            activity.setImage(saveImage(imageFile));
        }

        return activityService.save(activity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("badge") String badge,
            @RequestParam("description") String description,
            @RequestParam("available") String available,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        Activity existing = activityService.findById(id);
        if (existing != null) {
            existing.setTitle(title);
            existing.setBadge(badge);
            existing.setTag(badge);
            existing.setDescription(description);
            existing.setAvailable(Boolean.parseBoolean(available));

            if (imageFile != null && !imageFile.isEmpty()) {
                existing.setImage(saveImage(imageFile));
            }

            return ResponseEntity.ok(activityService.save(existing));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Activity> toggleActivityStatus(@PathVariable Long id) {
        Activity activity = activityService.findById(id);
        if (activity != null) {
            activity.setAvailable(!activity.isAvailable());
            return ResponseEntity.ok(activityService.save(activity));
        }
        return ResponseEntity.notFound().build();
    }

    private String saveImage(MultipartFile file) {
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            File destFile = new File(dir, fileName);
            file.transferTo(destFile);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
