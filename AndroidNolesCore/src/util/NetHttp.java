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

import com.itnoles.shared.util.base.HttpTransport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

final class NetHttp extends HttpTransport {
    private static final String LOG_TAG = "NetHttp";

    @Override
    public Response buildResponse(String url) throws IOException {
        return new Response(url);
    }

    final class Response extends LowLevelHttpResponse {
        private final HttpURLConnection mConnection;

        public Response(String url) throws IOException {
            this.mConnection = (HttpURLConnection) new URL(url).openConnection();
            mConnection.setUseCaches(false);
            mConnection.setInstanceFollowRedirects(false);
            mConnection.setReadTimeout(CONN_TIMEOUT);
            mConnection.setConnectTimeout(CONN_TIMEOUT);
        }

        @Override
        public BufferedInputStream execute() throws IOException {
            if (mConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.w(LOG_TAG, "Unexpected server response " + mConnection.getResponseMessage());
                return null;
            }
            return new BufferedInputStream(mConnection.getInputStream());
        }

        @Override
        public void disconnect() {
            mConnection.disconnect();
        }
    }
}