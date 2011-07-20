/*
 * Copyright (C) 2011 Jonathan Steele
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

import com.itnoles.shared.SportsConstants;

import java.util.regex.Pattern;

public class ParserUtils
{
    /** Used to sanitize a string to be {@link Uri} safe. */
    private static final Pattern SANITIZEPATTERN = Pattern.compile("[^a-z0-9-_]");
    private static final Pattern PARENTPATTERN = Pattern.compile("\\(.*?\\)");

    private static Time sTime = new Time();

    /**
     * Sanitize the given string to be {@link Uri} safe for building
     * {@link ContentProvider} paths.
     */
    public static String sanitizeId(String input)
    {
        return sanitizeId(input, false);
    }

    /**
     * Sanitize the given string to be {@link Uri} safe for building
     * {@link ContentProvider} paths.
     */
    public static String sanitizeId(String input, boolean stripParen)
    {
        if (input == null) {
            return null;
        }
        if (stripParen) {
            // Strip out all parenthetical statements when requested.
            input = PARENTPATTERN.matcher(input).replaceAll("");
        }
        return SANITIZEPATTERN.matcher(input.toLowerCase()).replaceAll("");
    }

    /**
     * Parse the given string as a RFC 3339 timestamp, returning the value as
     * milliseconds since the epoch.
     */
    public static long parseTime(String time)
    {
        sTime.parse3339(time);
        return sTime.toMillis(false);
    }

    public static long queryItemDetails(Uri uri, ContentResolver resolver)
    {
        final String[] projection = {SportsConstants.UPDATED};
        final Cursor cursor = resolver.query(uri, projection, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }
        finally {
            cursor.close();
        }
        return -1;
    }

    public static long queryDirUpdated(Uri uri, ContentResolver resolver)
    {
        final String[] projection = {"MAX(" + SportsConstants.UPDATED + ")"};
        final Cursor cursor = resolver.query(uri, projection, null, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getLong(0);
        }
        finally {
            cursor.close();
        }
    }
}