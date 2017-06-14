/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.basiclocationsample;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";

    protected boolean mIsUpdate;
    protected String mMsisdnFilePath = "/sdcard/xdrloc/xdrloc_msisdn.txt";
    protected Button mIsUpdateBtn;
    protected EditText mMsisdnEditText;
    protected String mAppDirPath = "/sdcard/xdrloc/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mIsUpdate = false;
        mMsisdnEditText = (EditText) findViewById(R.id.msisdn_text);

        createAppDir();

        String msisdnFromFile = readMSISDNFromFile();
        if (msisdnFromFile != null) {
            mMsisdnEditText.setText(msisdnFromFile);
        }

        mIsUpdateBtn = (Button) findViewById(R.id.update_btn);
        mIsUpdateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickLocBtn();
            }
        });

        if (isServiceRunning(XdrUpdateService.class)) {
            mIsUpdateBtn.setText("Stop Updates");
        }
    }

    public void onClickLocBtn() {
        Intent intent = new Intent(MainActivity.this, XdrUpdateService.class);
        String msisdnInput = readMSISDNFromFile();

        if (!isServiceRunning(XdrUpdateService.class)) {
            if (mMsisdnEditText.getText() == null) {
                Toast.makeText(this, "Please Insert your phone number!", Toast.LENGTH_LONG).show();
            } else {
                saveMSISDNFromFile();
                startService(intent);
                mIsUpdateBtn.setText("Stop Updates");
                mIsUpdate = true;
            }
        } else {

            stopService(intent);
            mIsUpdateBtn.setText("Start Updates");
            mIsUpdate = false;
        }
    }

    private String readMSISDNFromFile() {
        String result = null;
        BufferedReader br = null;
        FileReader fr = null;

        try {
            File file = new File(mMsisdnFilePath);

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
            result = null;
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (Exception ex) {
                result = null;
                ex.printStackTrace();
            }
        }

        return result;
    }

    private void saveMSISDNFromFile() {
        try {
            PrintWriter out = new PrintWriter(mMsisdnFilePath);
            out.print("");
            out.print(mMsisdnEditText.getText());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void createAppDir() {
        File file = new File(mAppDirPath);
        if (!file.exists()) {
            if (file.mkdir()) {
                Toast.makeText(this, "Directory is created!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to create directory!", Toast.LENGTH_LONG).show();
            }
        }
    }
}

