package com.example.jimshire.broncostore;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimshire on 10/3/17.
 *
 * This is the Adapter for Product class.
 */

public class ProductAdapter extends ArrayAdapter<Product> {
    private static final String TAG = "ProductAdapter";

    private List<Product> products = new ArrayList<Product>();

    public ProductAdapter(Activity context, ArrayList<Product> products) {
        super(context, 0, products);
    }

    public void updateProducts(List<Product> products) {
        this.products.addAll(products);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvName;
        TextView tvPrice;
        ImageView ivImage;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.adapter_product, parent, false);
            tvName = (TextView) convertView.findViewById(R.id.tvProductName);
            tvPrice = (TextView) convertView.findViewById(R.id.tvProductPrice);
            ivImage = (ImageView) convertView.findViewById(R.id.ivProductImage);
            convertView.setTag(new ViewHolder(tvName, tvPrice, ivImage));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            tvName = viewHolder.tvProductName;
            tvPrice = viewHolder.tvProductPrice;
            ivImage = viewHolder.ivProductImage;
        }

        final Product product = getItem(position);
        tvName.setText(product.getName());
        tvPrice.setText(Constant.CURRENCY+String.valueOf(product.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));
        Log.d(TAG, "Context package name: " + getContext().getPackageName());
        ivImage.setImageResource(getContext().getResources().getIdentifier(
                product.getImageName(), "drawable", getContext().getPackageName()));

        return convertView;
    }

    private static class ViewHolder {
        public final TextView tvProductName;
        public final TextView tvProductPrice;
        public final ImageView ivProductImage;

        public ViewHolder(TextView tvProductName, TextView tvProductPrice, ImageView ivProductImage) {
            this.tvProductName = tvProductName;
            this.tvProductPrice = tvProductPrice;
            this.ivProductImage = ivProductImage;
        }
    }
}