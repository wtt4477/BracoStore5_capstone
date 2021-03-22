package com.example.jimshire.broncostore;

import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wallet.MaskedWallet;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApplication extends Application implements BootstrapNotifier {

    private static final Map<String, List<String>> PLACES_BY_BEACONS;

    private static final String USER_PREFS = "com.google.android.gms.samples.wallet.USER_PREFS";
    private static final String KEY_USERNAME = "com.google.android.gms.samples.wallet.KEY_USERNAME";
    private SharedPreferences mPrefs;

    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("10735:22436", new ArrayList<String>() {{
            add("Minty");
        }});
        placesByBeacons.put("30857:62045)", new ArrayList<String>() {{
            add("Blueberry");
        }});
        placesByBeacons.put("59744:9419)", new ArrayList<String>() {{
            add("Icey");
        }});

        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private static final String TAG = "BroncoBeacon";
    BeaconManager mBeaconManager = null;
    private RegionBootstrap mRegionBootstrap;
    private BackgroundPowerSaver mBackgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private MaskedWallet mMaskedWallet = null;
    private String mBeaconID = null;
    private double mTip = 0.15;


    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        verifyBluetooth();
        //Setup beacon ranging service _ac
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215 ,i:4-19,i:20-21,i:22-23,p:24-24"));
        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        Region region = new Region("backgroundRegion", null, null, null);
        mRegionBootstrap = new RegionBootstrap(this, region);
        mBackgroundPowerSaver = new BackgroundPowerSaver(this);

        // hardcode psudo user name _ac
        mPrefs.edit().putString(KEY_USERNAME, "Jake Peralta").commit();
    }


    public String getAccountName() {
        return mPrefs.getString(KEY_USERNAME, null);
    }


    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "I see the light!");

        //Start wallet activity when seeing a beacon _ac
        if (!haveDetectedBeaconsSinceBoot) {
            Intent intent = new Intent(this, WalletActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra("REQUEST_FULL_WALLET", "FALSE");
            intent.putExtra("START_COUNTDOWN", "FALSE");
            this.startActivity(intent);
            haveDetectedBeaconsSinceBoot = true;
        } else {
            Log.d(TAG, "I see the light again.");
            Intent reEnterRegion = new Intent("reEnterRegion");
            sendBroadcast(reEnterRegion);
        }


    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "I don't see the light.");

        //Start Wallet activity to initiate request for the full wallet _ac
        if( mMaskedWallet!= null){
            Intent intent = new Intent(this, WalletActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra("REQUEST_FULL_WALLET", "TRUE");
            intent.putExtra("START_COUNTDOWN", "TRUE");
            startActivity(intent);

        }else {
            // No masked wallet, restart the wallet activity for masked wallet _ac
            if (haveDetectedBeaconsSinceBoot) {
                Intent intent = new Intent(this, WalletActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("REQUEST_FULL_WALLET", "FALSE");
                intent.putExtra("START_COUNTDOWN", "FALSE");
                this.startActivity(intent);
            } else {
                // No beacon seen in the begging, do nothing
                Log.d(TAG, "No beacon seen in the begging");
            }
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.d(TAG, "Searching for beacon...");
    }

    // Getter of beacon id if needed _ac
    public String getmBeaconID() {
        return mBeaconID;
    }

    // Setter of beacon id _ac
    public void setmBeaconID(String beaconID) {
        mBeaconID = mBeaconID;
    }

    // Setter of masked wallet _ac
    public void setmMaskedWallet(MaskedWallet maskedWallet) {
        mMaskedWallet = maskedWallet;
    }

    // Getter of masked wallet _ac
    public MaskedWallet getmMaskedWallet() {
        return mMaskedWallet;
    }

    public double getmTip() {
        return mTip;
    }

    public String getTipInString() {
        return Double.toString(mTip);
    }

    public void setmTip(double tip) { mTip = tip; }




    // Verify Bluetooth status
    private void verifyBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Sorry Bronco Store requires Bluetooth.", Toast.LENGTH_LONG).show();

        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Please enable bluetooth before proceed next.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
