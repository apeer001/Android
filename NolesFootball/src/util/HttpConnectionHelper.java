/*
 * Copyright (C) 2013 Jonathan Steele
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

package com.itnoles.nolesfootball.util;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class HttpConnectionHelper {
    private static final String TAG = LogUtils.makeLogTag(HttpConnectionHelper.class);

    private final Context mContext;

    public HttpConnectionHelper(Context context) {
        mContext = context;
    }

    public void execute(String urlString, HttpListener listener) {
        // Check to see if we are connected to a data or wifi network.
        if (!isOnline()) {
            return;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(0);
            listener.onPostExecute(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            LogUtils.LOGW(TAG, "Problem reading remote response for ", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public interface HttpListener {
        void onPostExecute(InputStreamReader is) throws IOException;
    }
}