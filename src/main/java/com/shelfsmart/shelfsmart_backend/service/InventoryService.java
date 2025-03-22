package com.shelfsmart.shelfsmart_backend.service;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import com.shelfsmart.shelfsmart_backend.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public InventoryItem addItem(InventoryItem item) {
        return inventoryRepository.save(item);
    }

    public List<InventoryItem> getAllItems() {
        return inventoryRepository.findAll();
    }
}