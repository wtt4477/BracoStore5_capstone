package com.example.jimshire.broncostore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimshire on 10/3/17.
 *
 * Some constant for testing the app.
 */

public final class Constant {
    public static final List<Integer> QUANTITY_LIST = new ArrayList<Integer>();

    static {
        for (int i = 1; i < 11; i++) QUANTITY_LIST.add(i);
    }

    //URL for HTTP GET and POST
    public static final String MENU_REQUEST_URL = "http://54.191.37.235:8000/menu_item/Blurr002/";
    public static final String ORDER_POST_URL = "http://54.191.37.235:8000/order_item/";
    public static final String CREATE_ORDER_URL = "http://54.191.37.235:8000/create_order/";
    public static final String PAYMENT_URL = "http://54.191.37.235:8000/pay/";

    //Currency sign
    public static final String CURRENCY = "$";

    public static String orderID;
    public static BigDecimal preTips;


}
