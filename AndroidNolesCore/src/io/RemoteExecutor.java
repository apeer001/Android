/*
 * Copyright (C) 2012 Jonathan Steele
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
import android.content.Context;

import com.itnoles.shared.util.XMLParserConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class RemoteExecutor {
    private final Context mContext;
    private final ContentResolver mResolver;

    public RemoteExecutor(Context context, ContentResolver resolver) {
        mContext = context;
        mResolver = resolver;
    }

    public void executeWithPullParser(String urlString, final XmlHandler handler, int size) {
        final XMLParserConnection connection = new XMLParserConnection(mContext);
        connection.execute(urlString, size, new XMLParserConnection.XMLParserListener() {
            public void onPostExecute(XmlPullParser parser) throws XmlPullParserException, IOException {
                handler.parseAndApply(parser, mResolver);
            }
        });
    }
}