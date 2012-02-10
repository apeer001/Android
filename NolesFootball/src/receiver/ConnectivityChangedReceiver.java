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

package com.itnoles.nolesfootball.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itnoles.nolesfootball.service.SyncService;
import com.itnoles.shared.util.NetworkUtils;
import com.itnoles.shared.util.PackageManagerWrapper;

public class ConnectivityChangedReceiver extends BroadcastReceiver {
	@Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isNetworkConnected(context)) {
            // The default state for this Receiver is disabled. it is only
            // enabled when a Service disables pending connectivity.
            final PackageManagerWrapper wrapper = new PackageManagerWrapper(context);
            wrapper.setDefaultComponentSetting(ConnectivityChangedReceiver.class);

            final Intent syncIntent = new Intent(context, SyncService.class);
            context.startService(syncIntent);
        }
    }
}