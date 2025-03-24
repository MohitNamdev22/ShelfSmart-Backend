package com.shelfsmart.shelfsmart_backend.repository;

import com.shelfsmart.shelfsmart_backend.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUserId(Long userId);
}