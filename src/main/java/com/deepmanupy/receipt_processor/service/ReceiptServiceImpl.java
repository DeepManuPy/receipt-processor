package com.deepmanupy.receipt_processor.service;

import com.deepmanupy.receipt_processor.model.Item;
import com.deepmanupy.receipt_processor.model.Receipt;
import com.deepmanupy.receipt_processor.repository.ReceiptRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Validated
public class ReceiptServiceImpl implements ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;


    @Override
    public String processReceipt(@Valid Receipt receiptObject) {
        Receipt savedReceipt = receiptRepository.save(receiptObject);

        return savedReceipt.getId();
    }

    @Override
    public Optional<Receipt> findReceiptById(@NotBlank String receiptId) {
        return receiptRepository.findById(receiptId);
    }

    @Override
    public int calculatePoints(@NotBlank String receiptId) {
        Optional<Receipt> receipt = findReceiptById(receiptId);
        if (receipt.isEmpty()) {
            throw new NoSuchElementException("No receipt found for that ID.");
        }
        Receipt receiptObject = receipt.get();

        int points = 0;
        points += calculateRetailerPoints(receiptObject.getRetailer());
        points += calculateReceiptTotalPoints(receiptObject.getTotal());
        points += calculateItemPairPoints(receiptObject.getItems());
        points += calculateItemDescriptionPoints(receiptObject.getItems());
        points += calculatePurchaseDatePoints(receiptObject.getPurchaseDate());
        points += calculatePurchaseTimePoints(receiptObject.getPurchaseTime());

        return points;
    }

    private int calculateRetailerPoints(String expression) {
        if (StringUtils.hasText(expression)) {
            return expression.replaceAll("[^a-zA-Z0-9]", "").length();
        }

        return 0;
    }

    private int calculateReceiptTotalPoints(String amount) {
        int points = isRoundDollar(amount) ? 50 : 0;
        points = isMultipleOfQuarter(amount) ? (points + 25) : points;
        return points;
    }

    private boolean isRoundDollar(String amount) {
        if (StringUtils.hasText(amount)) {
            return amount.matches("^\\d+\\.00$");
        }

        return false;
    }

    private boolean isMultipleOfQuarter(String amount) {
        if (StringUtils.hasText(amount)) {
            try {
                BigDecimal total = new BigDecimal(amount);
                BigDecimal quarter = new BigDecimal("0.25");

                // Check if total is divisible by 0.25 with no remainder
                BigDecimal[] divideAndRemainder = total.divideAndRemainder(quarter);
                return divideAndRemainder[1].compareTo(BigDecimal.ZERO) == 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return false;
    }

    private int calculateItemPairPoints(List<Item> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return 0;
        }

        return (itemList.size() / 2) * 5;
    }

    private int calculateItemDescriptionPoints(List<Item> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return 0;
        }

        int points = 0;
        for (Item item :
                itemList) {
            if (StringUtils.hasText(item.getShortDescription())) {
                int trimmedLength = item.getShortDescription().trim().length();
                if (trimmedLength % 3 == 0) {
                    points = points + calculateItemPricePoints(item.getPrice());
                }
            }
        }

        return points;
    }

    private int calculateItemPricePoints(String itemPrice) {
        if (StringUtils.hasText(itemPrice)) {
            try {
                // Parse the price string to BigDecimal
                BigDecimal priceValue = new BigDecimal(itemPrice);

                // Multiply by 0.2
                BigDecimal result = priceValue.multiply(new BigDecimal("0.2"));

                // Round up to the nearest integer
                return result.setScale(0, RoundingMode.CEILING).intValue();
            } catch (NumberFormatException e) {
                // Handle invalid price format
                return 0;
            }
        }
        return 0;
    }

    private int calculatePurchaseDatePoints(String purchaseDate) {
        if (StringUtils.hasText(purchaseDate) && purchaseDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            LocalDate date = LocalDate.parse(purchaseDate);
            if (date.getDayOfMonth() % 2 == 1) {
                return 6;
            }
        }
        return 0;
    }

    private int calculatePurchaseTimePoints(String purchaseTime) {
        if (StringUtils.hasText(purchaseTime) && purchaseTime.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
            LocalTime localTime = LocalTime.parse(purchaseTime);
            LocalTime startTime = LocalTime.of(14, 0);
            LocalTime endTime = LocalTime.of(16, 0);

            // Check if time is within the range, inclusive of start and exclusive of end
            return !localTime.isBefore(startTime) && localTime.isBefore(endTime) ? 10 : 0;
        }
        return 0;
    }
}
