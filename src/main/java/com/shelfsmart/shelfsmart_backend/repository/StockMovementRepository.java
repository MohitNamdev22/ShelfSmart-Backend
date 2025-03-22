package com.shelfsmart.shelfsmart_backend.repository;

import com.shelfsmart.shelfsmart_backend.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}