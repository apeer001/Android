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

package com.itnoles.shared;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class SportsApplication extends Application {
	private static final String LOG_TAG = "SportsApplication";

	@Override
	public void onCreate() {
		if (isDebugMode()) {
			try {
				final Class<?> strictMode = Class.forName("android.os.StrictMode");
				final Method enableDefaults = strictMode.getMethod("enableDefaults");
				enableDefaults.invoke(null);
            } catch (Exception e) {
                //The version of Android we're on doesn't have android.os.StrictMode
                //so ignore this exception
                Log.d(LOG_TAG, "Strict mode not available");
            }
        }
        super.onCreate();

        // Disable connection pooling for pre-Gingerbread
        if (!SportsConstants.SUPPORTS_GINGERBREAD) {
            System.setProperty("http.keepAlive", "false");
        }

        //enable the http response cache in a thread to avoid a strict mode violation
        new Thread() {
        	@Override
        	public void run() {
        		enableHttpResponseCache();
        	}
        }.start();
	}

	private void enableHttpResponseCache() {
		final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
		final File httpCacheDir = new File(getCacheDir(), "http");
		try {
			Class.forName("android.net.http.HttpResponseCache")
			     .getMethod("install", File.class, long.class)
			     .invoke(null, httpCacheDir, httpCacheSize);
		} catch (Exception httpResponseCacheNotAvailable) {
			try{
				com.integralblue.httpresponsecache.HttpResponseCache.install(httpCacheDir, httpCacheSize);
			} catch(IOException e) {
				Log.e(LOG_TAG, "Failed to set up com.integralblue.httpresponsecache.HttpResponseCache");
			}
		}
	}

	public boolean isDebugMode() {
	    // check if android:debuggable is set to true
	    if (getApplicationInfo() == null) {
	        // getApplicationInfo() returns null in unit tests
	        return true;
	    }

		final int applicationFlags = getApplicationInfo().flags;
		return (applicationFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}