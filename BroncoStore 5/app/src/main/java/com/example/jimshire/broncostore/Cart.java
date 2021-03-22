package com.example.jimshire.broncostore;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by Jimshire on 10/4/17.
 *
 * This is the shopping cart. A shopping cart should have a map of {@link Saleable} product
 * and number of product.
 */

public class Cart {

    private Map<Saleable, Integer> cartItemMap = new HashMap<Saleable, Integer>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private int totalQuantity = 0;

    /**
     * Add a quantity of a certain {@link Saleable} product to this shopping cart
     *
     * @param sellable the product will be added to this shopping cart
     * @param quantity the amount to be added
     */
    public void add(final Saleable sellable, int quantity) {
        if (cartItemMap.containsKey(sellable)) {
            cartItemMap.put(sellable, cartItemMap.get(sellable) + quantity);
        } else {
            cartItemMap.put(sellable, quantity);
        }

        totalPrice = totalPrice.add(sellable.getPrice().multiply(BigDecimal.valueOf(quantity)));
        totalQuantity += quantity;
    }

//    /**
//     * Remove a {@link Saleable} product from this shopping cart totally
//     *
//     * @param sellable the product to be removed
//     */
    public void remove(final Saleable sellable)  {
        int quantity = cartItemMap.get(sellable);
        cartItemMap.remove(sellable);
        totalPrice = totalPrice.subtract(sellable.getPrice().multiply(BigDecimal.valueOf(quantity)));
        totalQuantity -= quantity;
    }

    /**
     * Remove all products from this shopping cart
     */
    public void clear() {
        cartItemMap.clear();
        totalPrice = BigDecimal.ZERO;
        totalQuantity = 0;
    }

//    /**
//     * Get total cost of a {@link Saleable} product in this shopping cart
//     *
//     * @param sellable the product of interest which this method will return the total cost
//     * @return Total cost of the product
//     */
    public BigDecimal getCost(final Saleable sellable)  {
        return sellable.getPrice().multiply(BigDecimal.valueOf(cartItemMap.get(sellable)));
    }

    /**
     * Get total price of all products in this shopping cart
     *
     * @return Total price of all products in this shopping cart
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * Get total quantity of all products in this shopping cart
     *
     * @return Total quantity of all products in this shopping cart
     */
    public int getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * Get set of products in this shopping cart
     *
     * @return Set of {@link Saleable} products in this shopping cart
     */
    public Set<Saleable> getProducts() {
        return cartItemMap.keySet();
    }


    /**
     * Get a map of products to their quantities in the shopping cart
     *
     * @return A map from product to its quantity in this shopping cart
     */
    public Map<Saleable, Integer> getItemWithQuantity() {
        Map<Saleable, Integer> cartItemMap = new HashMap<Saleable, Integer>();
        cartItemMap.putAll(this.cartItemMap);
        return cartItemMap;
    }

    /**
     * Text indication for Total Quantity and Total price
     *
     * @return A map from product to its quantity in this shopping cart
     */

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (Entry<Saleable, Integer> entry : cartItemMap.entrySet()) {
            strBuilder.append(String.format("Product: %s, Unit Price: %f, Quantity: %d%n", entry.getKey().getName(), entry.getKey().getPrice(), entry.getValue()));
        }
        strBuilder.append(String.format("Total Quantity: %d, Total Price: %f", totalQuantity, totalPrice));

        return strBuilder.toString();
    }
}
