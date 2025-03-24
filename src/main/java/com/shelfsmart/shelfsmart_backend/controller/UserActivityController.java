package com.shelfsmart.shelfsmart_backend.controller;

import com.shelfsmart.shelfsmart_backend.model.User;
import com.shelfsmart.shelfsmart_backend.model.UserActivity;
import com.shelfsmart.shelfsmart_backend.service.UserActivityService;
import com.shelfsmart.shelfsmart_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserActivity>> getMyActivities() {
        User user = userService.getCurrentUser();
        List<UserActivity> activities = userActivityService.getUserActivities(user.getId());
        return ResponseEntity.ok(activities);
    }
}