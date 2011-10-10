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

import android.content.Context;
import android.util.Log;

import com.itnoles.shared.util.base.HttpTransport;

import java.io.IOException;
import java.io.InputStream
import java.net.HttpURLConnection;
import java.net.URL;

public final class NetHttp extends HttpTransport
{
    private static final String LOG_TAG = "NetHttp";

    private final HttpURLConnection connection;

    public NetHttp(Context context, String url)
    {
        super(context);
        this.connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);
        connection.setReadTimeout(CONN_TIMEOUT);
        connection.setConnectTimeout(CONN_TIMEOUT);
    }
    
    @Override
    public InputStream execute() throws IOException
    {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.w(LOG_TAG, "Unexpected server response " + connection.getResponseMessage());
        }
        return connection.getInputStream();
    }

    @Override
    public void shutdown()
    {
        connection.disconnect();
    }
}