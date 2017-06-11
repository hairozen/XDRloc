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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class XdrUpdateService extends Service {
    private static final String TAG = "XdrUpdateService";

    protected Location mLastLocation;
    protected TelephonyManager mTelephonyManager;
    protected LocationManager mLocationManager;
    protected GsmCellLocation mCellLocation;

    protected Calendar mStartTime;
    protected Calendar mCurrentTime;
    protected long mSecInterval;
    protected int mLastIntervalHour;
    protected int mLastIntervalMin;
    protected LocationData mLocationData;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isRunning = true;
        mSecInterval = 3600;
        mLocationData = new LocationData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "XDRloc Service Started!", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Service onStartCommand");
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

                    if (res == 0 && mCurrentTime.get(Calendar.MINUTE) != mLastIntervalMin) {
                        mLastIntervalHour = Calendar.getInstance().get(Calendar.HOUR);
                        mLastIntervalMin = Calendar.getInstance().get(Calendar.MINUTE);
                        checkLocation();
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
        Toast.makeText(this, "XDRloc Service Stopped!", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Service onDestroy");
    }

    private void checkLocation() {
        try {
            mCellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String networkOperation = mTelephonyManager.getNetworkOperator();
            if (mLastLocation != null) {
                mLocationData.setmLastUpdateTime(System.currentTimeMillis());
                mLocationData.setmCellId(mCellLocation.getCid());
                mLocationData.setmLac(mCellLocation.getLac());
                mLocationData.setmMCC(Integer.parseInt(networkOperation.substring(0, 3)));
                mLocationData.setmMNC(Integer.parseInt(networkOperation.substring(3)));
                mLocationData.setmImei(mTelephonyManager.getDeviceId());
                mLocationData.setmImsi(mTelephonyManager.getSubscriberId());

                mLocationData.writeLocationsOnSD();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }
    }

}
