package com.shelfsmart.shelfsmart_backend.controller;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import com.shelfsmart.shelfsmart_backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Restrict to ADMIN role
    public ResponseEntity<InventoryItem> addInventoryItem(@RequestBody InventoryItem item) {
        InventoryItem savedItem = inventoryService.addItem(item);
        return ResponseEntity.status(201).body(savedItem);
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventoryItems() {
        List<InventoryItem> items = inventoryService.getAllItems();
        return ResponseEntity.ok(items);
    }
}