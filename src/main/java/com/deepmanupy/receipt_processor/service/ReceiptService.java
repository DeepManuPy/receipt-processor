package com.deepmanupy.receipt_processor.service;

import com.deepmanupy.receipt_processor.model.Receipt;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
public interface ReceiptService {

    String processReceipt( @Valid Receipt receiptObject);

    Optional<Receipt> findReceiptById(@NotBlank String receiptId);

    int calculatePoints(@NotBlank String receiptId);
}
