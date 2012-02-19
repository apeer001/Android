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
import android.util.Xml;

import com.itnoles.shared.SportsConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class XMLParserWithNetHttp {
	private static final String LOG_TAG = "NetHttp";

    // 8 KB Buffer Size for BufferdInputStream
    private static final int BUFFER_SIZE = 8192;

	private XMLParserWithNetHttp() {}

	private static void executeWithConnection(HttpURLConnection connection, XMLPullParserManager manager) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.w(LOG_TAG, "Unexpected server response " + connection.getResponseMessage());
            return;
        }

        final InputStream input = new BufferedInputStream(connection.getInputStream(), BUFFER_SIZE);
        try {
	        final XmlPullParser parser = Xml.newPullParser();
            parser.setInput(input, null);
            manager.onPostExecute(parser);
        } catch(XmlPullParserException e) {
            Log.w(LOG_TAG, "Malformed response", e);
        } finally {
            if (input != null) {
                input.close();
            }
            if (!SportsConstants.SUPPORTS_GINGERBREAD) {
                connection.disconnect();
            }
        }
    }

    public static void execute(String url, XMLPullParserManager manager) {
        // Disable connection pooling for pre-Gingerbread
        if (!SportsConstants.SUPPORTS_GINGERBREAD) {
            System.setProperty("http.keepAlive", "false");
        }

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            executeWithConnection(connection, manager);
        } catch (IOException e) {
            Log.w(LOG_TAG, "Problem reading remote responses", e);
        }
    }

    public interface XMLPullParserManager {
		void onPostExecute(XmlPullParser parser);
	}
}