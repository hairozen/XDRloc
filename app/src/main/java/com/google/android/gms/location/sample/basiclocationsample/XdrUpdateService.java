package com.google.android.gms.location.sample.basiclocationsample;

import android.app.IntentService;
import android.content.Intent;


public class XdrUpdateService extends IntentService
{
    // Must create a default constructor
    public XdrUpdateService() {
        // Used to name the worker thread, important only for debugging.
        super("xdr-update");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
    }
}
