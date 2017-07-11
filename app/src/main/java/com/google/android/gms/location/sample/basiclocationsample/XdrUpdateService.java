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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    public LocationData mLocationData;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isRunning = true;
        mSecInterval = 3600000;
        mLocationData = new LocationData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "XDRloc Service Started!", Toast.LENGTH_LONG).show();
        //writeToFile("XDRloc Service Started!\n");
        Log.i(TAG, "Service onStartCommand");
        mStartTime = Calendar.getInstance();
        mCurrentTime = Calendar.getInstance();
        mLastIntervalHour = 0;
        mLastIntervalMin = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isRunning) {
//                    long currentMillis = mCurrentTime.getTimeInMillis();
//                    long startMillis = mStartTime.getTimeInMillis();
//                    long duration = TimeUnit.MILLISECONDS.toSeconds(currentMillis - startMillis);
//                    double res = duration % mSecInterval;

//                    if (res == 0 && mCurrentTime.get(Calendar.MINUTE) != mLastIntervalMin) {
//                        mLastIntervalHour = Calendar.getInstance().get(Calendar.HOUR);
//                        mLastIntervalMin = Calendar.getInstance().get(Calendar.MINUTE);

                    try {
                        checkLocation();
                        Thread.sleep(3600000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    }
//
//                    mCurrentTime = Calendar.getInstance();
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
        //writeToFile("XDRloc Service Stopped!\n");
        Log.i(TAG, "Service onDestroy");
    }

    private void checkLocation() {
        try {
//            writeToFile("*Check Location*\n");
            mCellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String networkOperation = mTelephonyManager.getNetworkOperator();
            if (mCellLocation != null) {
//                writeToFile("mLastLocation not null");
                mLocationData.setmLastUpdateTime(System.currentTimeMillis());
                //writeToFile(mLocationData.getmLastUpdateTime().toString());
                mLocationData.setmCellId(mCellLocation.getCid());
                //writeToFile(String.valueOf(mLocationData.getmCellId()));
                mLocationData.setmLac(mCellLocation.getLac());
                //writeToFile(String.valueOf(mLocationData.getmLac()));
                mLocationData.setmMCC(Integer.parseInt(networkOperation.substring(0, 3)));
                //writeToFile(String.valueOf(mLocationData.getmMCC()));
                mLocationData.setmMNC(Integer.parseInt(networkOperation.substring(3)));
                //writeToFile(String.valueOf(mLocationData.getmMNC()));
                mLocationData.setmImei(mTelephonyManager.getDeviceId());
                //writeToFile(mLocationData.getmImei());
                mLocationData.setmImsi(mTelephonyManager.getSubscriberId());
                //writeToFile(mLocationData.getmImsi());

//                String body = String.format("%s,%d,%d,%d,%d,%s,%s,%s;", mLocationData.getmLastUpdateTime().toString(), mLocationData.getmCellId(), mLocationData.getmLac(),
//                        mLocationData.getmMCC(), mLocationData.getmMNC(), mLocationData.getmImei(), mLocationData.getmImsi(), readMsidsnFromFile());
//                writeToFile("Test:::::" + body);
                mLocationData.writeLocationsOnSD();
            } else {
                //writeToFile("mLastLocation null");
            }
        } catch (Exception e) {
//            writeToFile(e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }
    }

    private void writeToFile(String log) {
        try {
            File outFile = new File("/sdcard/xdrloc/xdrloc_locations.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile, true /*append*/));
            writer.write(log);
            writer.close();
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the location to file.");
        }
    }

    private String readMsidsnFromFile() {
        String result = "";
        BufferedReader br = null;
        FileReader fr = null;

        try {
            File file = new File("/sdcard/xdrloc/xdrloc_msisdn.txt");

            StringBuilder stringBuilder = new StringBuilder();

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(file));

            while ((sCurrentLine = br.readLine()) != null) {
                stringBuilder.append(sCurrentLine);
            }

            result = stringBuilder.toString();

        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (Exception ex) {
                result = "";
                ex.printStackTrace();
            }
        }

        return result;
    }

}
