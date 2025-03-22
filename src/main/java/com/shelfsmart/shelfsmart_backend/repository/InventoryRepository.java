package com.shelfsmart.shelfsmart_backend.repository;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
}