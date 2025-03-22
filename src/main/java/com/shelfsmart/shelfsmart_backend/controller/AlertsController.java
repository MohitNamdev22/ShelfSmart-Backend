package com.shelfsmart.shelfsmart_backend.controller;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import com.shelfsmart.shelfsmart_backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alerts")
public class AlertsController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/expiry")
    public ResponseEntity<List<Object>> getExpiringItems() {
        List<InventoryItem> expiringItems = inventoryService.getExpiringItemsWithinDays(7);
        List<Object> response = expiringItems.stream()
                .map(item -> new Object() {
                    public final Long itemId = item.getId();
                    public final String name = item.getName();
                    public final String expiryDate = item.getExpiryDate().toString();
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}