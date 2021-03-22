package com.example.jimshire.broncostore;

import java.math.BigDecimal;

/**
 * Created by Jimshire on 10/4/17.
 *
 * This interface is for every product can be added into the cart
 *
 */

public interface Saleable {
    BigDecimal getPrice();
    String getName();
}
