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
import android.util.Log;

import java.lang.reflect.Method;

public class SportsApplication extends Application {
    private static final String LOG_TAG = "SportsApplication";

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
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
    }
}