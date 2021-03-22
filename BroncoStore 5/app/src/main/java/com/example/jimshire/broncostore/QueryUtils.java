package com.example.jimshire.broncostore;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimshire on 10/29/17.
 *
 * This is a query class for HTTP GET method.
 */

public final class QueryUtils {

    private static final String LOG_TAG = "QueryUtils";

    /** Sample JSON response for a query */
    //private static final String SAMPLE_JSON_RESPONSE = "[{\"Product_id\": \"BL001\", \"Product_name\": \"Garlic Fries\", \"Price\": \"6.00\", \"Description\": \"Garlic fries\"}, {\"Product_id\": \"BL002\", \"Product_name\": \"Chicken Wings\", \"Price\": \"6.00\", \"Description\": \"\\\"Blurr\\\" habanero chili glaze\"}, {\"Product_id\": \"BL007\", \"Product_name\": \"BBQ chicken Sandwich\", \"Price\": \"8.00\", \"Description\": \"BBQ Chicken Sandwich\"}, {\"Product_id\": \"BL008\", \"Product_name\": \"Roasted Pork Sandwich\", \"Price\": \"8.00\", \"Description\": \"Roasted Pork Sandwich\"}, {\"Product_id\": \"BL008\", \"Product_name\": \"Roasted Pork Sandwich\", \"Price\": \"8.00\", \"Description\": \"Roasted Pork Sandwich\"}, {\"Product_id\": \"BL008\", \"Product_name\": \"Roasted Pork Sandwich\", \"Price\": \"8.00\", \"Description\": \"Roasted Pork Sandwich\"}, {\"Product_id\": \"BL008\", \"Product_name\": \"Roasted Pork Sandwich\", \"Price\": \"8.00\", \"Description\": \"Roasted Pork Sandwich\"}]";
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the product JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<Product> extractProductsFromJSON(String productJSON) {

        if(TextUtils.isEmpty(productJSON)){
            return null;
        }
        // Create an empty ArrayList that we can start adding product to
        ArrayList<Product> products = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

//             build up a list of Product objects with the corresponding data.
            JSONArray productArray = new JSONArray(productJSON);

            for(int i = 0; i < productArray.length(); i++){
                JSONObject currentProduct = productArray.getJSONObject(i);
                String productID = currentProduct.getString("Product_id");
                String name = currentProduct.getString("Product_name");
                Double price = currentProduct.getDouble("Price");
                String description = currentProduct.getString("Description");
                Product product = new Product(productID, name, BigDecimal.valueOf(price), description, "image");
                products.add(product);
            }



        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the product JSON results", e);
        }

        // Return the list of products
        return products;
    }

    /**
     * Query the dataset and return a list of {@link Product} objects.
     */
    public static List<Product> fetchProductData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of Products
        List<Product> products = extractProductsFromJSON(jsonResponse);

        return products;
    }
}
