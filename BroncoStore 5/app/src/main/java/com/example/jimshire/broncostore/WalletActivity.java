package com.example.jimshire.broncostore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.example.jimshire.broncostore.Constants.REQUEST_CODE_MASKED_WALLET;
import static com.example.jimshire.broncostore.Constants.REQUEST_CODE_RESOLVE_LOAD_FULL_WALLET;


public class WalletActivity extends FragmentActivity implements BeaconConsumer,
        GoogleApiClient.OnConnectionFailedListener{
	protected static final String TAG = "BroncoWalletActivity";
	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
	private GoogleApiClient mGoogleApiClient;
	private SupportWalletFragment mWalletFragment;
	private BroadcastReceiver mReenteringMessageReceiver =null;
	private int mCounter;
    private TextView tvBeaconInfo;
    private TextView tvMessage;
    private Button btnSelect;
    private Button btnCloseApp;
	private TextView tvCountdown;
	private EditText etTip;
	private Button btnSetTip;
	private String defaultTip;
	private String tipSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		verifyBluetooth();
		defaultTip = ((MyApplication) this.getApplicationContext()).getTipInString();
		setContentView(R.layout.activity_wallet);
        tvMessage = findViewById(R.id.textViewMessage);
		tvBeaconInfo = findViewById(R.id.textViewBeaconInfo);
		btnSelect = findViewById(R.id.btnSelectCard);
        btnCloseApp = findViewById(R.id.btnCloseApp);
		tvCountdown = findViewById(R.id.tvCountdown);
		etTip = findViewById(R.id.editTextTip);
		etTip.setText(defaultTip);
		btnSetTip = findViewById(R.id.btnSetTip);

		beaconManager.bind(this);
		mReenteringMessageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};

		String flag = getIntent().getStringExtra("START_COUNTDOWN").trim();
		// If start countdown,
		if(flag.equals("TRUE")){
			//Display countdown timer and hide select button and beacon info.
			tvMessage.setText("You just left the beacon range");
			tvCountdown.setVisibility(View.VISIBLE);
			etTip.setVisibility(View.VISIBLE);
			btnSelect.setVisibility(View.GONE);
			tvBeaconInfo.setVisibility(View.GONE);
			btnCloseApp.setVisibility(View.GONE);
			// Create countdown timer, show countdown timer _ac
			startCountdown();

		}else{
			btnCloseApp.setVisibility(View.GONE);
			etTip.setVisibility(View.GONE);
			btnSetTip.setVisibility(View.GONE);
		}

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wallet.API, new Wallet.WalletOptions.Builder()
						.setEnvironment(Constants.WALLET_ENVIRONMENT)
						.build())
				.enableAutoManage(this,this)
				.build();

		registerReceiver(mReenteringMessageReceiver, new IntentFilter("reEnterRegion"));

	}

	private void startCountdown() {
		new CountDownTimer(10000, 1000){
			public void onTick(long millisUntilFinished){
				tvCountdown.setText("Payment will be automatically processed in " + millisUntilFinished / 1000 +" seconds");
			}
			public  void onFinish(){
				requestFullWallet();
			}
		}.start();
	}



	private void requestFullWallet() {
        MaskedWallet maskedWallet = ((MyApplication) this.getApplicationContext()).getmMaskedWallet();
        ItemInfo itemInfo = Constants.ITEMS_FOR_SALE[0];
        FullWalletRequest fullWalletRequest = WalletUtil.createFullWalletRequest(itemInfo,
                maskedWallet.getGoogleTransactionId());

        Wallet.Payments.loadFullWallet(mGoogleApiClient, fullWalletRequest,
                REQUEST_CODE_RESOLVE_LOAD_FULL_WALLET);

		tvCountdown.setText("FINISH!!");
		btnCloseApp.setVisibility(View.VISIBLE);
    }

    // BLE Permissions
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_COARSE_LOCATION: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d(TAG, "coarse location permission granted");
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}
				return;
			}
		}
	}

	// BLE verification
	private void verifyBluetooth() {
		try {
			if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
						System.exit(0);
					}
				});
				builder.show();
			}
		}
		catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
					System.exit(0);
				}

			});
			builder.show();
		}
	}

	public void onSelectCardClicked(View view) {
		// Start Android Pay _ac
		// [START fragment_style_and_options]
		WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
				.setBuyButtonText(WalletFragmentStyle.BuyButtonText.BUY_WITH)
				.setBuyButtonAppearance(WalletFragmentStyle.BuyButtonAppearance.ANDROID_PAY_DARK)
				.setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT);

		WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
				.setEnvironment(Constants.WALLET_ENVIRONMENT)
				.setFragmentStyle(walletFragmentStyle)
				.setTheme(WalletConstants.THEME_LIGHT)
				.setMode(WalletFragmentMode.BUY_BUTTON)
				.build();
		mWalletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);
		// [END fragment_style_and_options]

		// Now initialize the Wallet Fragment _ac
		String accountName = ((MyApplication) getApplication()).getAccountName();
		MaskedWalletRequest maskedWalletRequest;
		int mItemId = 0;
		// Direct integration _ac
		maskedWalletRequest = WalletUtil.createMaskedWalletRequest(
				Constants.ITEMS_FOR_SALE[mItemId],
				getString(R.string.public_key));

		// [START params_builder]
		WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
				.setMaskedWalletRequest(maskedWalletRequest)
				.setMaskedWalletRequestCode(REQUEST_CODE_MASKED_WALLET)
				.setAccountName(accountName);
		mWalletFragment.initialize(startParamsBuilder.build());

		// add Wallet fragment to the UI
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.dynamic_wallet_button_fragment, mWalletFragment)
				.commit();
		// [END params_builder]

		Log.d(TAG, "Wallet fragment added");

	}

    @Override
    public void onResume() {
        super.onResume();
		if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
	}

    @Override
    public void onPause() {
        super.onPause();
		if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		beaconManager.unbind(this);
		//unregisterReceiver(mReenteringMessageReceiver);
	}

	@Override
	public void onBeaconServiceConnect() {
		beaconManager.setRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
				if (beacons.size() > 0) {
					Beacon firstBeacon = beacons.iterator().next();
					Log.d(TAG,"The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
					String id = firstBeacon.getId2().toString()+":"+firstBeacon.getId3().toString();
					setBeaconID(id);
				}
			}
		});

		try {
			beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {   }
	}

	private void setBeaconID(String id) {
		((MyApplication) this.getApplicationContext()).setmBeaconID(id);
		// Access beaconID using the following line _ac
		// String id = ((MyApplication) this.getApplicationContext()).getmBeaconID();
	}

    // [START on_activity_result]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // retrieve the error code, if available
        int errorCode = -1;
        if (data != null) {
            errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
        }
        switch (requestCode) {
            case REQUEST_CODE_MASKED_WALLET:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (data != null) {
                            //Get and setup masked wallet in application _ac
                            MaskedWallet maskedWallet =
                                    data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                            ((MyApplication) this.getApplicationContext()).setmMaskedWallet(maskedWallet);

                            // Close wallet activity once we get masked wallet _ac
                            Log.d(TAG, "Close the activity");
                            this.finish();
                        }
                        break;
                    case WalletConstants.RESULT_ERROR:
                        handleError(errorCode);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;

                    default:
                        handleError(errorCode);
                        break;
                }
                break;
            case REQUEST_CODE_RESOLVE_LOAD_FULL_WALLET:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (data != null && data.hasExtra(WalletConstants.EXTRA_FULL_WALLET)) {
                            FullWallet fullWallet = data.getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
                            PaymentMethodToken token = fullWallet.getPaymentMethodToken();
                            if (token != null) {
                                // Got the token. Update wallet view for countdown. _ac
                                Log.d(TAG, "PaymentMethodToken:" + token.getToken().replace('\n', ' '));

                                // Hide tip and tip btn_ac
                                etTip.setVisibility(View.GONE);
								btnSetTip.setVisibility(View.GONE);

								//final post to the server here
								Map<String, String> postData = new HashMap<>();
								postData.put("Order_id", Constant.orderID);
								postData.put("Pre_tips", Constant.preTips.toString());
								postData.put("Tips", tipSet);
								postData.put("Total", Integer.toString(Constant.preTips.intValue() * Integer.parseInt(tipSet)));
								PaymentAsyncTask task = new PaymentAsyncTask(postData);
								task.execute(Constant.PAYMENT_URL);
                            }
                        } else if (data != null && data.hasExtra(WalletConstants.EXTRA_MASKED_WALLET)) {
                            Log.d(TAG, "onActivityResult: failed");
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        handleError(errorCode);
                        break;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    // [END on_activity_result]

    @SuppressLint("StringFormatInvalid")
	protected void handleError(int errorCode) {
        switch (errorCode) {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                Toast.makeText(this, getString(R.string.spending_limit_exceeded, errorCode),
                        Toast.LENGTH_LONG).show();
                break;
            case WalletConstants.ERROR_CODE_INVALID_PARAMETERS:
            case WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE:
            case WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_MERCHANT_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE:
            case WalletConstants.ERROR_CODE_UNSUPPORTED_API_VERSION:
            case WalletConstants.ERROR_CODE_UNKNOWN:
            default:
                String errorMessage = getString(R.string.google_wallet_unavailable) + "\n" +
                        getString(R.string.error_code, errorCode);
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
        Toast.makeText(this, "Google Play Services error", Toast.LENGTH_SHORT).show();
    }

    public void onCloseBroncoStoreClicked(View view) {
        this.finishAffinity();
    }

	public void onSetTipClicked(View view) {
		String tip  = etTip.getText().toString();
		((MyApplication) this.getApplicationContext()).setmTip(Double.parseDouble(tip));
        tipSet = ((MyApplication) this.getApplicationContext()).getTipInString();
        tvMessage.setText("Tips set to "+ tipSet +". Thank you!");

        // Close the number input panel _ac
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etTip.getWindowToken(), 0);
	}


	//Final post after user make a payment
	private class PaymentAsyncTask extends AsyncTask<String, Void, Void> {

		// This is the JSON body of the post
		JSONObject postData;
		// This is a constructor that allows you to pass in the JSON body
		public PaymentAsyncTask(Map<String, String> postData) {
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

					String response = inputStream.toString();

					// From here you can convert the string to JSON with whatever JSON parser you like to use
					// After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
				} else {
					// Status code is not 200
					// Do something to handle the error
				}

			} catch (Exception e){
				Log.d("POST", e.getLocalizedMessage());
			}
			return null;
		}

	}
}
