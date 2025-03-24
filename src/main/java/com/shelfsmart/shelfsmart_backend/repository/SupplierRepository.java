package com.shelfsmart.shelfsmart_backend.repository;

import com.shelfsmart.shelfsmart_backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}