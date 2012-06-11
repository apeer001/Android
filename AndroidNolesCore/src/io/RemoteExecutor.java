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

package com.itnoles.shared.io;

import android.content.ContentResolver;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class RemoteExecutor {
    private static final String LOG_TAG = "RemoteExecutor";

    private final ContentResolver mResolver;

    public RemoteExecutor(ContentResolver resolver) {
        this.mResolver = resolver;
    }

    public void executeWithPullParser(String urlString, XmlHandler handler, int size) {
        HttpsURLConnection urlConnection = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            final InputStream input = new BufferedInputStream(urlConnection.getInputStream(), size);
            try {
                final XmlPullParser parser = Xml.newPullParser();
                parser.setInput(input, null);
                handler.parseAndApply(parser, mResolver);
            } catch (XmlPullParserException e) {
                Log.w(LOG_TAG, "Malformed response for ", e);
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        } catch (IOException e) {
            Log.w("Problem reading remote response for ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}