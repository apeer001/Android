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
import android.os.Build;
import android.text.format.Time;

/**
 * General helpers method
 */
public final class Utils {
    public static final String UPDATED = "updated";

    private static Time sTime = new Time();

    private Utils() {
    }

    public static boolean isGingerbread() {
        // Can use static final constants like HONEYCOMB, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Parse the given string as a RFC 3339 timestamp, returning the value as
     * milliseconds since the epoch.
     */
    public static long parseTime(String time) {
        sTime.parse3339(time);
        return sTime.toMillis(false);
    }

    /**
     * Query and return the {@link SportsConstants#UPDATED} time for the requested
     * {@link Uri}. Expects the {@link Uri} to reference a single item.
     */
    public static long queryItemUpdated(Uri uri, ContentResolver resolver) {
        final String[] projection = {UPDATED};
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
     * Query and return the newest {@link SportsConstants#UPDATED} time for all
     * entries under the requested {@link Uri}. Expects the {@link Uri} to
     * reference a directory of several items.
     */
    public static long queryDirUpdated(Uri uri, ContentResolver resolver) {
        final String[] projection = {"MAX(" + UPDATED + ")"};
        final Cursor cursor = resolver.query(uri, projection, null, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }
    }
}