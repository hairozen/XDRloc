package com.google.android.gms.location.sample.basiclocationsample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class XdrUpdateService extends Service {
    private static final String TAG = "XdrUpdateService";

    protected Location mLastLocation;
    protected TelephonyManager mTelephonyManager;
    protected LocationManager mLocationManager;
    protected GsmCellLocation mCellLocation;

    protected double mLatitude;
    protected double mLongitude;
    protected int mCellId;
    protected int mLac;
    protected int mMCC;
    protected int mMNC;
    protected String mLastUpdateTime;
    protected Calendar mStartTime;
    protected Calendar mCurrentTime;
    protected long mSecInterval;
    protected int mLastIntervalHour;
    protected int mLastIntervalMin;
    protected String mOutputFilePath;
    protected File mOutFile;

    private boolean isRunning = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isRunning = true;
        mSecInterval = 100;
        mOutputFilePath = "/sdcard/xdrloc_locations.txt";
        createNoteFile();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service onStartCommand", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Service onStartCommand");
        generateNoteOnSD("Servicer Strating!");
        mStartTime = Calendar.getInstance();
        mCurrentTime = Calendar.getInstance();
        mLastIntervalHour = 0;
        mLastIntervalMin = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isRunning) {
                    long currentMillis = mCurrentTime.getTimeInMillis();
                    long startMillis = mStartTime.getTimeInMillis();
                    long duration = TimeUnit.MILLISECONDS.toSeconds(currentMillis - startMillis);
                    double res = duration % mSecInterval;

                    if (res == 0 /*&& mCurrentTime.get(Calendar.MINUTE) != mLastIntervalMin*/) {
                        mLastIntervalHour = Calendar.getInstance().get(Calendar.HOUR);
                        mLastIntervalMin = Calendar.getInstance().get(Calendar.MINUTE);
                        generateNoteOnSD("Before run");
                        //checkLocation();
                    }

                    mCurrentTime = Calendar.getInstance();
                }
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "Service onDestroy", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Service onDestroy");
    }

    private void checkLocation() {
        try {
            generateNoteOnSD("checkLocation run");
            mCellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String networkOperation = mTelephonyManager.getNetworkOperator();
            if (mLastLocation != null) {
                mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
                mCellId = mCellLocation.getCid();
                mLac = mCellLocation.getLac();
                mMCC = 0;//Integer.parseInt(networkOperation.substring(0, 3));
                mMNC = 0;//Integer.parseInt(networkOperation.substring(3));
                generateNoteOnSD(String.format("%s,%f,%f,%d,%d,%d,%d:", mLastUpdateTime, mLatitude, mLongitude, mCellId, mLac, mMCC, mMNC));
                Log.i(TAG, "Location Updated: " + mLastUpdateTime);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }
    }

    public void createNoteFile() {
        try {
            File mOutFile = new File(mOutputFilePath);
            if (!mOutFile.exists()) {
                mOutFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateNoteOnSD(String body) {
        try {
            if (mOutFile == null) {
                createNoteFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(mOutFile, true /*append*/));
            writer.write(body);
            writer.close();
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the TestFile.txt file.");
        }
    }

}
