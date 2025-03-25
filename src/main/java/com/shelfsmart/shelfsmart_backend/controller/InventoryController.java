package com.shelfsmart.shelfsmart_backend.controller;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import com.shelfsmart.shelfsmart_backend.model.User;
import com.shelfsmart.shelfsmart_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private InventoryTrendService inventoryTrendService;

    @Autowired
    private GeminiService geminiService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryItem> addInventoryItem(@RequestBody InventoryItem item) {
        InventoryItem savedItem = inventoryService.addItem(item);
        User user = userService.getCurrentUser();
        userActivityService.logActivity(user, "ITEM_ADDED", "Added item: " + item.getName());
        return ResponseEntity.status(201).body(savedItem);
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventoryItems() {
        List<InventoryItem> items = inventoryService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getInventoryItemById(@PathVariable Long id) {
        return inventoryService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItem>> getLowStockItems() {
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryItem> updateInventoryItem(@PathVariable Long id, @RequestBody InventoryItem item) {
        try {
            InventoryItem updatedItem = inventoryService.updateItem(id, item);
            User user = userService.getCurrentUser();
            userActivityService.logActivity(user, "ITEM_UPDATED", "Updated item: " + item.getName());
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long id) {
        try {
            InventoryItem item = inventoryService.getItemById(id)
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            inventoryService.deleteItem(id);
            User user = userService.getCurrentUser();
            userActivityService.logActivity(user, "ITEM_DELETED", "Deleted item: " + item.getName());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/consume")
    public ResponseEntity<InventoryItem> consumeInventoryItem(@PathVariable Long id, @RequestBody ConsumeRequest request) {
        try {
            InventoryItem updatedItem = inventoryService.consumeItem(id, request.getQuantity());
            User user = userService.getCurrentUser();
            userActivityService.logActivity(user, "ITEM_CONSUMED",
                    "Consumed " + request.getQuantity() + " units of item: " + updatedItem.getName());
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryItem>> searchInventoryItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String stockLevel) {
        List<InventoryItem> items = inventoryService.searchItems(name, category, stockLevel);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getInventorySuggestions() {
        String trends = inventoryTrendService.getInventoryTrends();
        System.out.println("Trends Data: " + trends);
        String prompt = "Based on this inventory and usage data: " + trends +
                ", suggest items to restock or add to the inventory.";
        Map<String, List<Map<String, String>>> suggestions = geminiService.generateSuggestions(prompt);
        return ResponseEntity.ok(suggestions);
    }
}

class ConsumeRequest {
    private int quantity;

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}