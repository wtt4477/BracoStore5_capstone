package com.example.jimshire.broncostore;

/**
 * Created by Jimshire on 10/4/17.
 *
 * Cart item class. Including getter and setter for cart quantity and the product.
 *
 */

public class CartItem {
    private Product product;
    private int quantity;


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
