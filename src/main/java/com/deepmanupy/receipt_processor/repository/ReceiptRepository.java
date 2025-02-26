package com.deepmanupy.receipt_processor.repository;

import com.deepmanupy.receipt_processor.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, String> {
    // Basic CRUD methods are automatically provided: save(), findByID()
}
