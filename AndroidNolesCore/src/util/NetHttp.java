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

package com.itnoles.shared.util;

import android.util.Log;

import com.itnoles.shared.SportsConstants;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class NetHttp {
    private static final String LOG_TAG = "NetHttp";

    // 8 KB Buffer Size for BufferedInputStream
    private static final int BUFFER_SIZE = 8192;

    private final HttpURLConnection mConnection;

    public NetHttp(String url) throws IOException {
        // Disable connection pooling for pre-Gingerbread
        if (!SportsConstants.SUPPORTS_GINGERBREAD) {
            System.setProperty("http.keepAlive", "false");
        }

        HttpURLConnection.setFollowRedirects(false);
        this.mConnection = (HttpURLConnection) new URL(url).openConnection();
    }

    /**
     * Get InputStream with buffered from HttpURLConnection.
     * @return if responseCode is not OK, null else inputstream
     */
    public InputStream getInputStream() throws IOException {
        if (mConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.w(LOG_TAG, "Unexpected server response message " + mConnection.getResponseMessage()
                + " with response code" + mConnection.getResponseCode());
            return null;
        }
        return new BufferedInputStream(mConnection.getInputStream(), BUFFER_SIZE);
    }

    public void close() {
        if (!SportsConstants.SUPPORTS_GINGERBREAD) {
            mConnection.disconnect();
        }
    }
}