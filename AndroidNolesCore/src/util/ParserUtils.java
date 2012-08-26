/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itnoles.shared.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ParserUtils {
    private static Time sTime = new Time();

    private ParserUtils() {}

    /**
     * Query and return the updated time for the requested
     * {@link Uri}. Expects the {@link Uri} to reference a single item.
     */
    public static long queryItemUpdated(Uri uri, ContentResolver resolver) {
        final String[] projection = {"updated"};
        final Cursor cursor = resolver.query(uri, projection, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } finally {
            cursor.close();
        }
        return -1;
    }

    /**
     * Query and return the newest updated time for all
     * entries under the requested {@link Uri}. Expects the {@link Uri} to
     * reference a directory of several items.
     */
    public static long queryDirUpdated(Uri uri, ContentResolver resolver) {
        final String[] projection = {"MAX(updated)"};
        final Cursor cursor = resolver.query(uri, projection, null, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }
    }

    // Processes title tags in the feed.
    public static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        final String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    // For the tags extracts their text values.
    public static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /*
     * Process the updated tag in the feed and parse the given string
     * as a RFC 3339 timestamp, returning the value as milliseconds since
     * the epoch.
     */
    public static long readUpdated(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "updated");
        sTime.parse3339(readText(parser));
        final long updated = sTime.toMillis(false);
        parser.require(XmlPullParser.END_TAG, null, "updated");
        return updated;
    }
}