package com.shelfsmart.shelfsmart_backend.service;

import com.shelfsmart.shelfsmart_backend.model.InventoryItem;
import com.shelfsmart.shelfsmart_backend.model.StockMovement;
import com.shelfsmart.shelfsmart_backend.repository.InventoryRepository;
import com.shelfsmart.shelfsmart_backend.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryTrendService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public String getInventoryTrends() {
        List<StockMovement> movements = stockMovementRepository.findAll();
        List<InventoryItem> items = inventoryRepository.findAll();
        StringBuilder trends = new StringBuilder();

        // Current inventory status
        trends.append("Current Inventory:\n");
        for (InventoryItem item : items) {
            trends.append(String.format("%s: %d units (Threshold: %d, Expiry: %s)\n",
                    item.getName(), item.getQuantity(), item.getThreshold() != null ? item.getThreshold() : 0,
                    item.getExpiryDate() != null ? item.getExpiryDate().toString() : "N/A"));
        }

        // Usage trends from stock movements
        Map<Long, List<StockMovement>> byItem = movements.stream()
                .collect(Collectors.groupingBy(StockMovement::getItemId));
        trends.append("\nUsage Trends:\n");
        for (Long itemId : byItem.keySet()) {
            List<StockMovement> itemMoves = byItem.get(itemId);
            InventoryItem item = items.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElse(null);
            if (item == null) continue;

            int consumed = itemMoves.stream()
                    .filter(m -> "CONSUMED".equals(m.getMovementType()))
                    .mapToInt(StockMovement::getQuantityChanged)
                    .sum();
            LocalDateTime earliest = itemMoves.stream()
                    .map(StockMovement::getTimestamp)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());
            long days = ChronoUnit.DAYS.between(earliest, LocalDateTime.now());
            double rate = days > 0 ? (double) consumed / days : 0;

            trends.append(String.format("%s: Consumed %d units, Rate: %.2f units/day, Stock Left: %d\n",
                    item.getName(), consumed, rate, item.getQuantity()));
        }

        return trends.toString();
    }
}