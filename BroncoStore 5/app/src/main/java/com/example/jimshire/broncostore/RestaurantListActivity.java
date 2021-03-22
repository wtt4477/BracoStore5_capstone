package com.example.jimshire.broncostore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Jimshire on 10/4/17.
 *
 * Here is two sample list for the main page.
 */

public class RestaurantListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list);

        TextView coffeeShop = (TextView) findViewById(R.id.coffeeshop);

        // Set a click listener on that View
        coffeeShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent coffeeShopIntent = new Intent(RestaurantListActivity.this, MainActivity.class);
                startActivity(coffeeShopIntent);
            }
        });

        TextView restaurant = (TextView) findViewById(R.id.restaurant);

        restaurant.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent restaurantIntent = new Intent(RestaurantListActivity.this, MainActivity.class);
                startActivity(restaurantIntent);
            }
        });
    }
}
