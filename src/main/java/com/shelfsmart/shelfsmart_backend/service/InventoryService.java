package com.shelfsmart.shelfsmart_backend.service;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import com.shelfsmart.shelfsmart_backend.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<InventoryItem> getItemById(Long id) {
        return inventoryRepository.findById(id);
    }

    public InventoryItem updateItem(Long id, InventoryItem updatedItem) {
        Optional<InventoryItem> existingItem = inventoryRepository.findById(id);
        if (existingItem.isPresent()) {
            InventoryItem item = existingItem.get();
            item.setName(updatedItem.getName());
            item.setQuantity(updatedItem.getQuantity());
            item.setExpiryDate(updatedItem.getExpiryDate());
            item.setCategory(updatedItem.getCategory());
            item.setSupplierInfo(updatedItem.getSupplierInfo());
            item.setThreshold(updatedItem.getThreshold());
            return inventoryRepository.save(item);
        } else {
            throw new RuntimeException("Inventory item with ID " + id + " not found");
        }
    }

    public void deleteItem(Long id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Inventory item with ID " + id + " not found");
        }
    }
}