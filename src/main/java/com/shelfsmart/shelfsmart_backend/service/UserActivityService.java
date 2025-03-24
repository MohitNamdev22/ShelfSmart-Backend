package com.shelfsmart.shelfsmart_backend.service;

import com.shelfsmart.shelfsmart_backend.model.User;
import com.shelfsmart.shelfsmart_backend.model.UserActivity;
import com.shelfsmart.shelfsmart_backend.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityService {
    @Autowired
    private UserActivityRepository userActivityRepository;

    public void logActivity(User user, String action, String description) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setAction(action);
        activity.setDescription(description);
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    public List<UserActivity> getUserActivities(Long userId) {
        return userActivityRepository.findByUserId(userId);
    }
}