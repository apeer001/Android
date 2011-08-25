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

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;

import com.itnoles.shared.SportsConstants;

import java.util.regex.Pattern;

public class ParserUtils
{
    /** Used to sanitize a string to be {@link Uri} safe. */
    private static final Pattern SANITIZE_PATTERN = Pattern.compile("[^a-z0-9-_]");
    private static final Pattern PARENT_PATTERN = Pattern.compile("\\(.*?\\)");

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
            input = PARENT_PATTERN.matcher(input).replaceAll("");
        }
        return SANITIZE_PATTERN.matcher(input.toLowerCase()).replaceAll("");
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