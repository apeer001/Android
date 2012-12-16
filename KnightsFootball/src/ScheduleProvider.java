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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.knightfootball;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itnoles.shared.provider.AbstractScheduleProvider;

public class ScheduleProvider extends AbstractScheduleProvider {
    public static final String CONTENT_AUTHORITY = "com.itnoles.knightfootball.provider";
    public static final Uri SCHEDULE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/schedule");
    public static final Uri STAFF_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/staff");

    /**
     * Allocate the UriMatcher object that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    static {
        URIMATCHER.addURI(CONTENT_AUTHORITY, "schedule", SCHEDULE);
        URIMATCHER.addURI(CONTENT_AUTHORITY, "schedule/*", SCHEDULE_ID);

        URIMATCHER.addURI(CONTENT_AUTHORITY, "staff", STAFF);
        URIMATCHER.addURI(CONTENT_AUTHORITY, "staff/*", STAFF_ID);
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URIMATCHER.match(uri);
        switch (match) {
            case SCHEDULE:
                db.insertOrThrow(SCHEDULE_TXT, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.withAppendedPath(SCHEDULE_CONTENT_URI, values.getAsString("title"));
            case STAFF:
                db.insertOrThrow(STAFF_TXT, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.withAppendedPath(STAFF_CONTENT_URI, values.getAsString("title"));
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI_LOG + uri);
        }
    }
}