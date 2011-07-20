/*
 * Copyright 2011 Google Inc.
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

package com.itnoles.shared.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.itnoles.shared.service.SyncService;

/**
 * This Receiver class is designed to listen for changes in connectivity.
 *
 * When we lose connectivity the relevant Service classes will automatically
 * disable syncing with google worksheet.
 *
 * This class will restart the sync service.
 */
public class ConnectivityChangedReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Check if we are connected to an active data network.
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            final PackageManager pm = context.getPackageManager();
            final ComponentName connectivityReceiver = new ComponentName(context, ConnectivityChangedReceiver.class);
            // The default state for this Receiver is disabled. it is only
            // enabled when a Service disables updates pending connectivity.
            pm.setComponentEnabledSetting(connectivityReceiver, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);

            final Intent syncIntent = new Intent(context, SyncService.class);
            context.startService(syncIntent);
        }
    }
}