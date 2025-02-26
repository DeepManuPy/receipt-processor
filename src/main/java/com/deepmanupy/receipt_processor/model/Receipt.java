package com.deepmanupy.receipt_processor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Entity
public class Receipt {

    @Id
    private String id;
    @NotBlank(message = "Retailer is mandatory")
    @Pattern(regexp = "^[\\w\\s\\-&]+$", message = "Invalid retailer")
    private String retailer;
    @NotBlank
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "Purchase date must be a valid date in format YYYY-MM-DD")
    private String purchaseDate;
    @NotBlank
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$",
            message = "Purchase time must be in 24-hour format HH:MM")
    private String purchaseTime;
    @NotEmpty
    @ElementCollection
    private List<Item> items;
    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "Price must match the pattern 0.00")
    private String total;

    public String getId() {
        return id;
    }

    public String getRetailer() {
        return retailer;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getTotal() {
        return total;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setPurchaseTime(String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @PrePersist
    private void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id='" + id + '\'' +
                ", retailer='" + retailer + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", purchaseTime='" + purchaseTime + '\'' +
                ", items=" + items +
                ", total='" + total + '\'' +
                '}';
    }

    @AssertTrue(message = "Invalid date - this date does not exist in the calendar")
    private boolean isValidPurchaseDate() {
        if (purchaseDate == null || !purchaseDate.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
            return true; // Skip this validation if basic format check fails
        }

        try {
            LocalDate.parse(purchaseDate); // This will throw if date is invalid (e.g., Feb 30)
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
