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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.io.IOException;

public class RemoteExecutor {
    private static final String LOG_TAG = "RemoteExecutor";

    private final HttpClient mHttpClient;
    private final ContentResolver mResolver;

    public RemoteExecutor(HttpClient httpClient, ContentResolver resolver) {
        this.mHttpClient = httpClient;
        this.mResolver = resolver;
    }

    public void executeWithPullParser(String url, XmlHandler handler) {
        final HttpGet request = new HttpGet(url);
        try {
            final HttpResponse response = mHttpClient.execute(request);
            final int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                Log.w(LOG_TAG, "Unexpected server response " + response.getStatusLine() + " for " + request.getRequestLine());
            }
            final InputStream input = response.getEntity().getContent();
            try {
                final XmlPullParser parser = Xml.newPullParser();
                parser.setInput(input, null);
                handler.parseAndApply(parser, mResolver);
            } catch (XmlPullParserException e) {
                Log.w(LOG_TAG, "Malformed response for " + request.getRequestLine(), e);
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        } catch (IOException e) {
            Log.w("Problem reading remote response for " + request.getRequestLine(), e);
        }
    }
}