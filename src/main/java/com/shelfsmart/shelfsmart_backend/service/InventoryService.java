package com.shelfsmart.shelfsmart_backend.service;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import com.shelfsmart.shelfsmart_backend.model.StockMovement;
import com.shelfsmart.shelfsmart_backend.model.User;
import com.shelfsmart.shelfsmart_backend.repository.InventoryRepository;
import com.shelfsmart.shelfsmart_backend.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private UserService userService;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public InventoryItem addItem(InventoryItem item) {
        InventoryItem savedItem = inventoryRepository.save(item);
        stockMovementRepository.save(new StockMovement(
                savedItem.getId(),
                getCurrentUserId(),
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
            item.setSupplier(updatedItem.getSupplier());
            item.setThreshold(updatedItem.getThreshold());
            InventoryItem savedItem = inventoryRepository.save(item);
            if (quantityChange != 0) {
                stockMovementRepository.save(new StockMovement(
                        id,
                        getCurrentUserId(),
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
                    getCurrentUserId(),
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

    public InventoryItem consumeItem(Long id, int quantity) {
        Optional<InventoryItem> itemOptional = inventoryRepository.findById(id);
        if (itemOptional.isPresent()) {
            InventoryItem item = itemOptional.get();
            if (item.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for item ID " + id);
            }
            item.setQuantity(item.getQuantity() - quantity);
            InventoryItem updatedItem = inventoryRepository.save(item);
            stockMovementRepository.save(new StockMovement(
                    id,
                    getCurrentUserId(),
                    -quantity,
                    "CONSUMED",
                    LocalDateTime.now()
            ));
            return updatedItem;
        } else {
            throw new RuntimeException("Inventory item with ID " + id + " not found");
        }
    }

    public List<InventoryItem> searchItems(String name, String category, String stockLevel) {
        return inventoryRepository.findAll().stream()
                .filter(item -> (name == null || item.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(item -> (category == null || item.getCategory().equalsIgnoreCase(category)))
                .filter(item -> {
                    if (stockLevel == null) return true;
                    if ("low".equalsIgnoreCase(stockLevel)) {
                        return item.getThreshold() != null && item.getQuantity() < item.getThreshold();
                    }
                    if ("normal".equalsIgnoreCase(stockLevel)) {
                        return item.getThreshold() == null || item.getQuantity() >= item.getThreshold();
                    }
                    return true; // Ignore invalid stockLevel values
                })
                .collect(Collectors.toList());
    }
}