package com.example.jimshire.broncostore;

/**
 * Created by Jimshire on 10/4/17.
 *
 * Helper for retrieving the shopping cart
 */

public class CartHelper {
    private static Cart cart = new Cart();

    /**
     * Retrieve the shopping cart. Call this before perform any manipulation on the shopping cart.
     *
     * @return the shopping cart
     */
    public static Cart getCart() {
        if (cart == null) {
            cart = new Cart();
        }
        return cart;
    }
}
