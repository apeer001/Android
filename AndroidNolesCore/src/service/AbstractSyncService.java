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

package com.itnoles.shared.service;

import android.app.IntentService;
import android.content.ContentResolver;

import com.itnoles.shared.io.RemoteExecutor;
import com.itnoles.shared.util.NetworkUtils;

public abstract class AbstractSyncService extends IntentService {
    protected static final String TAG = "SyncService";

    protected RemoteExecutor mRemoteExecutor;
    protected NetworkUtils mNetwork;

    public AbstractSyncService() {
       super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final ContentResolver resolver = getContentResolver();
        mRemoteExecutor = new RemoteExecutor(resolver);
        mNetwork = new NetworkUtils(this);
    }
}