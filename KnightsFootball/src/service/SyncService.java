/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.knightfootball.service;

import android.content.Intent;
import android.content.pm.PackageManager;

import com.itnoles.knightfootball.receiver.ConnectivityChangedReceiver;
import com.itnoles.shared.service.AbstractSyncService;
import com.itnoles.shared.io.WorksheetsHandler;
import com.itnoles.shared.util.PackageManagerWrapper;

public class SyncService extends AbstractSyncService {
    private static final String WORKSHEET_URL = "https://spreadsheets.google.com/feeds/worksheets/0AvRfIfyMiQAGdFowOThSZGs5OXpQMnpvdEJSc29TWHc/public/basic";

    @Override
    protected void onHandleIntent(Intent intent) {
    	// Check to see if we are connected to a data or wifi network.
        if (mNetwork.isNetworkConnected()) {
           try {
                mRemoteExecutor.executeWithPullParser(WORKSHEET_URL, new WorksheetsHandler(mRemoteExecutor));
            } finally {
                mTransport.shutdown();
            }
        } else {
            // Enable the Connectivity Changed Receiver to listen for connection to a network
            final PackageManagerWrapper wrapper = new PackageManagerWrapper(this);
            wrapper.setComponentEnabledSetting(ConnectivityChangedReceiver.class, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        }
    }
}