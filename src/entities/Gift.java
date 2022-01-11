package entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public final class Gift {
    private final String productName;
    private final Double price;
    private final String category;
    @JsonIgnore
    private int quantity;

    public Gift(final String productName, final Double price,
                final String category, final int quantity) {
        this.productName = productName;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public Double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }
}
