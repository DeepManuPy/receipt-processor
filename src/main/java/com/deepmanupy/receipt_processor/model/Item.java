package com.deepmanupy.receipt_processor.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Embeddable
public class Item {
    @NotBlank
    @Pattern(regexp = "^[\\w\\s\\-]+$",
            message = "Invalid character in description")
    private String shortDescription;
    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d{2}$",
            message = "Price must match the pattern 0.00")
    private String price;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
