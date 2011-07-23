/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itnoles.shared.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.itnoles.shared.io.RemoteExecutor;
import com.itnoles.shared.io.WorksheetsHandler;
import com.itnoles.shared.R;
import com.itnoles.shared.receiver.ConnectivityChangedReceiver;

public class SyncService extends IntentService
{
    private static final String TAG = "SyncService";
    private RemoteExecutor mRemoteExecutor;
    protected ConnectivityManager mConnectManager;

    public SyncService()
    {
       super(TAG);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mRemoteExecutor = new RemoteExecutor(this, getContentResolver());
        mConnectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "onHandleIntent(intent=" + intent.toString() + ")");

        // Check to see if we are connected to a data network.
        final NetworkInfo activeNetwork = mConnectManager.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            // Enable the Connectivity Changed Receiver to listen for connection to a network
            final PackageManager pm = getPackageManager();
            final ComponentName connectivityReceiver = new ComponentName(this, ConnectivityChangedReceiver.class);
            pm.setComponentEnabledSetting(connectivityReceiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            return;
        }

        final long startRemote = System.currentTimeMillis();
        mRemoteExecutor.executeWithPullParser(getResources().getString(R.string.worksheet_url), new WorksheetsHandler(mRemoteExecutor));
        Log.d(TAG, "remote sync took " + (System.currentTimeMillis() - startRemote) + "ms");
    }
}