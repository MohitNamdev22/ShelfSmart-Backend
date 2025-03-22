package com.shelfsmart.shelfsmart_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementId;

    private Long itemId;
    private int quantityChanged;
    private String movementType; // e.g., "ADDED", "UPDATED", "DELETED"
    private LocalDateTime timestamp;

    // Constructors
    public StockMovement() {}
    public StockMovement(Long itemId, int quantityChanged, String movementType, LocalDateTime timestamp) {
        this.itemId = itemId;
        this.quantityChanged = quantityChanged;
        this.movementType = movementType;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getMovementId() { return movementId; }
    public void setMovementId(Long movementId) { this.movementId = movementId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public int getQuantityChanged() { return quantityChanged; }
    public void setQuantityChanged(int quantityChanged) { this.quantityChanged = quantityChanged; }
    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}