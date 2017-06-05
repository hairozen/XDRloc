/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.basiclocationsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        {

    protected static final String TAG = "MainActivity";

    protected boolean mIsUpdate;

    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mCellIdLabel;
    protected String mLastUpdateLabel;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView mCellIdText;
    protected TextView mLastUpdatedText;
    protected Button mIsUpdateBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mCellIdLabel = getResources().getString(R.string.cellid_label);
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateLabel = getResources().getString(R.string.last_updtae_label);
        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));
        mCellIdText = (TextView) findViewById((R.id.cell_id_text));
        mLastUpdatedText = (TextView) findViewById((R.id.last_update_text));
        mIsUpdate = false;

        mIsUpdateBtn = (Button) findViewById(R.id.update_btn);
        mIsUpdateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickLocBtn();
            }
        });
    }

    public void onClickLocBtn(){
        Intent intent = new Intent(MainActivity.this, XdrUpdateService.class);

        if(!mIsUpdate) {
            startService(intent);
            mIsUpdateBtn.setText("Stop Updates");
            mIsUpdate = true;
        }
        else
        {
            stopService(intent);
            mIsUpdateBtn.setText("Start Updates");
            mIsUpdate = false;
        }
    }
}
