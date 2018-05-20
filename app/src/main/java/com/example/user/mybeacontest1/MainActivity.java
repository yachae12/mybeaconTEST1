package com.example.user.mybeacontest1;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button button;

    BluetoothAdapter mBluetoothAdapter;

    BluetoothLeScanner mBluetoothLeScanner;

    BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private static final int PERMISSIONS = 100;

    //BeaconAdapter beaconAdapter;

    ListView beaconListView;

    ScanSettings.Builder mScanSettings;

    List<ScanFilter> scanFilters;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREAN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS);
        beaconListView = (ListView) findViewById(R.id.beaconListView);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mScanSettings = new ScanSettings.Builder();
        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        ScanSettings scanSettings = mScanSettings.build();

        scanFilters = new Vector<>();
        ScanFilter.Builder scanFilter = new ScanFilter.Builder();
        scanFilter.setDeviceAddress("F0:F8:F2:6F:55:A3"); //ex) 00:00:00:00:00:00
        ScanFilter scan = scanFilter.build();
        scanFilters.add(scan);
        //mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);

        button =(Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startScanning();
            }
        });

    }

    private void startScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.startScan(mScanCallback);

            }
        });
    }

    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            try {
                ScanRecord scanRecord = result.getScanRecord();
                Log.d("getTxPowerLevel()", scanRecord.getTxPowerLevel() + "");
                Log.d("onScanResult()", result.getDevice().getAddress() + "\n" + result.getRssi() + "\n" + result.getDevice().getName()
                        + "\n" + result.getDevice().getBondState() + "\n" + result.getDevice().getType());

                String device = result.getDevice().toString();

                if (device.equals("F0:F8:F2:6F:55:A3") && result.getRssi() < -60 ){
                    stopScanning();
                    android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("알림").setMessage("이용해주셔서 감사합니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                            .create().show();
                }
                final ScanResult scanResult = result;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /*ble 스캔 멈주기*/
        public void stopScanning() {
            //System.out.println("stopping scanning");
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(mScanCallback);

                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("onBatchScanResults", results.size() + "");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("onScanFailed()", errorCode + "");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeScanner.stopScan(mScanCallback);
    }
}

