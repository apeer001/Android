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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.*;

public class XMLParserConnection {
    private static final String LOG_TAG = LogUtils.makeLogTag(XMLParserConnection.class);

    private final String mUserAgent;

    public XMLParserConnection(Context context) {
        mUserAgent = buildUserAgent(context);
    }

    public void execute(String urlString, int size, XMLParserListener listener) {
        HttpURLConnection urlConnection = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", mUserAgent);
            final InputStream input = new BufferedInputStream(urlConnection.getInputStream(), size);
            try {
                final XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(input, null);
                parser.nextTag();
                listener.onPostExecute(parser);
            } catch (XmlPullParserException e) {
                LogUtils.LOGW(LOG_TAG, "Malformed response for ", e);
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        } catch (IOException e) {
            LogUtils.LOGW(LOG_TAG, "Problem reading remote response for ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
    private static String buildUserAgent(Context context) {
        String versionName = "unknown";
        int versionCode = 0;

        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
            versionCode = info.versionCode;
        } catch (NameNotFoundException ignored) {}

        return context.getPackageName() + "/" + versionName + " (" + versionCode + ") (gzip)";
    }

    public interface XMLParserListener {
        void onPostExecute(XmlPullParser parser) throws XmlPullParserException, IOException;
    }
}