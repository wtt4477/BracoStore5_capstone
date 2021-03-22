package com.example.jimshire.broncostore;

import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class WalletUtil {

    private static final BigDecimal MICROS = new BigDecimal(1000000d);

    private WalletUtil() {}

    public static MaskedWalletRequest createMaskedWalletRequest(ItemInfo itemInfo, String publicKey) {
        // Validate the public key
        if (publicKey == null || publicKey.contains("REPLACE_ME")) {
            throw new IllegalArgumentException("Invalid public key, see README for instructions.");
        }

        // Create direct integration parameters
        // [START direct_integration_parameters]
        PaymentMethodTokenizationParameters parameters =
                PaymentMethodTokenizationParameters.newBuilder()
                    .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.NETWORK_TOKEN)
                    .addParameter("publicKey", publicKey)
                    .build();
        // [END direct_integration_parameters]

        return createMaskedWalletRequest(parameters);
    }


    private static MaskedWalletRequest createMaskedWalletRequest(PaymentMethodTokenizationParameters parameters) {

        // [START masked_wallet_request]
        MaskedWalletRequest request = MaskedWalletRequest.newBuilder()
                .setMerchantName(Constants.MERCHANT_NAME)
                .setPhoneNumberRequired(true)
                .setShippingAddressRequired(true)
                .setCurrencyCode(Constants.CURRENCY_CODE_USD)
                //ac: masked wallet is constructed before ordering so cartTotal ="0"
                .setEstimatedTotalPrice("0")
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(Constants.CURRENCY_CODE_USD)
                        .setTotalPrice("0")
                        //.setLineItems(lineItems)
                        .build())
                .setPaymentMethodTokenizationParameters(parameters)
                .build();

        return request;
        // [END masked_wallet_request]
    }


    private static List<LineItem> buildLineItems(ItemInfo itemInfo, boolean isEstimate) {
        List<LineItem> list = new ArrayList<LineItem>();
        String itemPrice = toDollars(itemInfo.priceMicros);

        list.add(LineItem.newBuilder()
                .setCurrencyCode(Constants.CURRENCY_CODE_USD)
                .setDescription(itemInfo.name)
                .setQuantity("1")
                .setUnitPrice(itemPrice)
                .setTotalPrice(itemPrice)
                .build());

        String shippingPrice = toDollars(
                isEstimate ? itemInfo.estimatedShippingPriceMicros : itemInfo.shippingPriceMicros);

        list.add(LineItem.newBuilder()
                .setCurrencyCode(Constants.CURRENCY_CODE_USD)
                .setDescription(Constants.DESCRIPTION_LINE_ITEM_SHIPPING)
                .setRole(LineItem.Role.SHIPPING)
                .setTotalPrice(shippingPrice)
                .build());

        String tax = toDollars(
                isEstimate ? itemInfo.estimatedTaxMicros : itemInfo.taxMicros);

        list.add(LineItem.newBuilder()
                .setCurrencyCode(Constants.CURRENCY_CODE_USD)
                .setDescription(Constants.DESCRIPTION_LINE_ITEM_TAX)
                .setRole(LineItem.Role.TAX)
                .setTotalPrice(tax)
                .build());

        return list;
    }

    private static String calculateCartTotal(List<LineItem> lineItems) {
        BigDecimal cartTotal = BigDecimal.ZERO;

        // Calculate the total price by adding up each of the line items
        for (LineItem lineItem: lineItems) {
            BigDecimal lineItemTotal = lineItem.getTotalPrice() == null ?
                    new BigDecimal(lineItem.getUnitPrice())
                            .multiply(new BigDecimal(lineItem.getQuantity())) :
                    new BigDecimal(lineItem.getTotalPrice());

            cartTotal = cartTotal.add(lineItemTotal);
        }

        return cartTotal.setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    public static FullWalletRequest createFullWalletRequest(ItemInfo itemInfo,
                                                            String googleTransactionId) {

        List<LineItem> lineItems = buildLineItems(itemInfo, false);

        String cartTotal = calculateCartTotal(lineItems);

        // [START full_wallet_request]
        FullWalletRequest request = FullWalletRequest.newBuilder()
                .setGoogleTransactionId(googleTransactionId)
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(Constants.CURRENCY_CODE_USD)
                        .setTotalPrice(cartTotal)
                        .setLineItems(lineItems)
                        .build())
                .build();
        // [END full_wallet_request]

        return request;
    }

    public static NotifyTransactionStatusRequest createNotifyTransactionStatusRequest(
            String googleTransactionId, int status) {
        return NotifyTransactionStatusRequest.newBuilder()
                .setGoogleTransactionId(googleTransactionId)
                .setStatus(status)
                .build();
    }

    private static String toDollars(long micros) {
        return new BigDecimal(micros).divide(MICROS)
                .setScale(2, RoundingMode.HALF_EVEN).toString();
    }
}
