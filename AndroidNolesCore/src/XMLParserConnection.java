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

package com.itnoles.shared;

import android.content.Context;
import android.net.ConnectivityManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class XMLParserConnection {
    private static final String TAG = LogUtils.makeLogTag(XMLParserConnection.class);

    private static XmlPullParserFactory sXmlPullParserFactory;

    private final Context mContext;

    static {
        try {
            sXmlPullParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            LogUtils.LOGE(TAG, "Could not instantiate XmlPullParserFactory", e);
        }
    }

    public XMLParserConnection(Context context) {
        mContext = context;
    }

    public void execute(String urlString, XMLParserListener listener) {
        // Check to see if we are connected to a data or wifi network.
        if (!isOnline()) {
            return;
        }

        HttpURLConnection connection = null;
        try {
            final URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(0);
            connection.setRequestProperty("User-Agent", System.getProperty("http.agent"));

            final XmlPullParser parser = sXmlPullParserFactory.newPullParser();
            parser.setInput(new InputStreamReader(connection.getInputStream()));
            listener.onPostExecute(parser);
        } catch (XmlPullParserException e) {
            LogUtils.LOGW(TAG, "Malformed response for ", e);
        } catch (IOException e) {
            LogUtils.LOGW(TAG, "Problem reading remote response for ", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean isOnline() {
        final ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public interface XMLParserListener {
        void onPostExecute(XmlPullParser parser) throws XmlPullParserException, IOException;
    }
}