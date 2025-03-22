package com.shelfsmart.shelfsmart_backend.service;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import com.shelfsmart.shelfsmart_backend.model.StockMovement;
import com.shelfsmart.shelfsmart_backend.repository.InventoryRepository;
import com.shelfsmart.shelfsmart_backend.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    public InventoryItem addItem(InventoryItem item) {
        InventoryItem savedItem = inventoryRepository.save(item);
        stockMovementRepository.save(new StockMovement(
                savedItem.getId(),
                savedItem.getQuantity(),
                "ADDED",
                LocalDateTime.now()
        ));
        return savedItem;
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
            int quantityChange = updatedItem.getQuantity() - item.getQuantity();
            item.setName(updatedItem.getName());
            item.setQuantity(updatedItem.getQuantity());
            item.setExpiryDate(updatedItem.getExpiryDate());
            item.setCategory(updatedItem.getCategory());
            item.setSupplierInfo(updatedItem.getSupplierInfo());
            item.setThreshold(updatedItem.getThreshold());
            InventoryItem savedItem = inventoryRepository.save(item);
            if (quantityChange != 0) {
                stockMovementRepository.save(new StockMovement(
                        id,
                        quantityChange,
                        "UPDATED",
                        LocalDateTime.now()
                ));
            }
            return savedItem;
        } else {
            throw new RuntimeException("Inventory item with ID " + id + " not found");
        }
    }

    public void deleteItem(Long id) {
        Optional<InventoryItem> item = inventoryRepository.findById(id);
        if (item.isPresent()) {
            int quantity = item.get().getQuantity();
            inventoryRepository.deleteById(id);
            stockMovementRepository.save(new StockMovement(
                    id,
                    -quantity,
                    "DELETED",
                    LocalDateTime.now()
            ));
        } else {
            throw new RuntimeException("Inventory item with ID " + id + " not found");
        }
    }

    public List<InventoryItem> getLowStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(item -> item.getThreshold() != null && item.getQuantity() < item.getThreshold())
                .collect(Collectors.toList());
    }

    public List<InventoryItem> getExpiringItemsWithinDays(int days) {
        LocalDate thresholdDate = LocalDate.now().plusDays(days);
        return inventoryRepository.findAll().stream()
                .filter(item -> item.getExpiryDate() != null && !item.getExpiryDate().isAfter(thresholdDate))
                .collect(Collectors.toList());
    }
}