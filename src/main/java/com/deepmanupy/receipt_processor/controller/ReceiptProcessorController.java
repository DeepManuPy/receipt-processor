package com.deepmanupy.receipt_processor.controller;

import com.deepmanupy.receipt_processor.model.Receipt;
import com.deepmanupy.receipt_processor.service.ReceiptService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Validated
public class ReceiptProcessorController {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptProcessorController.class);
    @Autowired
    private ReceiptService receiptService;

    @PostMapping("/receipts/process")
    public Map<String, String> processReceipt(@Valid @RequestBody Receipt receipt){
        logger.info("Processing receipt");
        String receiptId = receiptService.processReceipt(receipt);
        return Map.of("id", receiptId);
    }

    @GetMapping("/receipts/{receiptId}/points")
    public Map<String, Integer> getPoints(@PathVariable @NotBlank String receiptId){
        logger.info("Calculating points for receipt:{}", receiptId);
        int points = receiptService.calculatePoints(receiptId);
        return Map.of("points", points);
    }
}
