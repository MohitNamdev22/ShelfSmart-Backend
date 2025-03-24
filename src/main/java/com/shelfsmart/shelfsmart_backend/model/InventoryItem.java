package com.shelfsmart.shelfsmart_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "inventory_item")
@Data
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer quantity;
    private LocalDate expiryDate;
    private String category;
    private Integer threshold;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier; // Replaced supplierInfo
}