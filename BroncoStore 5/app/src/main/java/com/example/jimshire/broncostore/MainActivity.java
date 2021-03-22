package com.example.jimshire.broncostore;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";

    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvViewShoppingCart = (TextView) findViewById(R.id.tvViewShoppingCart);
        SpannableString content = new SpannableString(getText(R.string.shopping_cart));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvViewShoppingCart.setText(content);
        tvViewShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
            }
        });


        ListView lvProducts = (ListView) findViewById(R.id.lvProducts);
        lvProducts.addHeaderView(getLayoutInflater().inflate(R.layout.product_list_header, lvProducts, false));

        productAdapter = new ProductAdapter(this, new ArrayList<Product>());

        lvProducts.setAdapter(productAdapter);

        lvProducts.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Product product = productAdapter.getItem(position - 1);
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // Start the AsyncTask to fetch the products data
        ProductAsyncTask task = new ProductAsyncTask();
        task.execute(Constant.MENU_REQUEST_URL);

        Map<String, String> postData = new HashMap<>();
        postData.put("Res_id", "R001");
        postData.put("Table_id", "T1");
        CreateOrderAsyncTask createOrderTask = new CreateOrderAsyncTask(postData);
        createOrderTask.execute(Constant.CREATE_ORDER_URL);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class ProductAsyncTask extends AsyncTask<String, Void, List<Product>>{

        @Override
        protected List<Product> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Product> result = QueryUtils.fetchProductData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Product> data) {
            productAdapter.clear();

            if (data != null && !data.isEmpty()) {
                productAdapter.updateProducts(data);
            }
        }
    }

    private class CreateOrderAsyncTask extends AsyncTask<String, Void, Void> {

        // This is the JSON body of the post
        JSONObject postData;
        // This is a constructor that allows you to pass in the JSON body
        public CreateOrderAsyncTask(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        @Override
        protected Void doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            try {
                URL url = new URL(urls[0]);

                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();
                if (statusCode ==  200) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    String jsonResponse = "";
                    //String response = inputStream.toString();
                    jsonResponse = readFromStream(inputStream);


                    JSONObject currentOrder = new JSONObject(jsonResponse);
                    Constant.orderID = currentOrder.getString("Order_id");
                    //Log.d("POST-orderID", orderID);

                    // From here you can convert the string to JSON with whatever JSON parser you like to use
                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
                } else {
                    // Status code is not 200
                    // Do something to handle the error
                    Log.d("POST", "Status code is not 200");
                }

            } catch (Exception e){
                Log.d("POST", e.getLocalizedMessage());
            }
            return null;
        }

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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

}

