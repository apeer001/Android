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

package com.itnoles.shared.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.util.Log;

import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.provider.ScheduleContract.Schedule;
import com.itnoles.shared.provider.ScheduleContract.Link;
import com.itnoles.shared.provider.ScheduleContract.Staff;

import java.util.ArrayList;

public class ScheduleProvider extends ContentProvider
{
    private static final String TAG = "ScheduleProvider";

    private SQLiteDatabase mScheduleDB;
    private static final String DATABASE_NAME = "schedule.db";
    private static final int DATABASE_VERSION = 1;

    private static final UriMatcher URIMATCHER = buildUriMatcher();
    private static final int SCHEDULE = 100;
    private static final int SCHEDULE_ID = 101;

    private static final int LINK = 200;
    private static final int LINK_ID = 201;

    private static final int STAFF = 300;
    private static final int STAFF_ID = 301;

    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    private static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SportsConstants.CONTENT_AUTHORITY;
        matcher.addURI(authority, "schedule", SCHEDULE);
        matcher.addURI(authority, "schedule/*", SCHEDULE_ID);

        matcher.addURI(authority, "link", LINK);
        matcher.addURI(authority, "link/*", LINK_ID);

        matcher.addURI(authority, "staff", STAFF);
        matcher.addURI(authority, "staff/*", STAFF_ID);
        return matcher;
    }

    @Override
    public boolean onCreate()
    {
        final Context context = getContext();

        final ScheduleDatabaseHelper dbHelper = new ScheduleDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        try {
            mScheduleDB = dbHelper.getWritableDatabase();
        }
        catch (SQLiteException e) {
            mScheduleDB = null;
            Log.e(TAG, "Database Opening exception");
        }

        return mScheduleDB == null;
    }

     /** {@inheritDoc} */
    @Override
    public String getType(Uri uri)
    {
        final int match = URIMATCHER.match(uri);
        switch (match) {
        case SCHEDULE:
            return "vnd.android.cursor.dir/vnd.itnoles.schedule";
        case SCHEDULE_ID:
            return "vnd.android.cursor.item/vnd.itnoles.schedule";
        case LINK:
            return "vnd.android.cursor.dir/vnd.itnoles.link";
        case LINK_ID:
            return "vnd.android.cursor.item/vnd.itnoles.link";
        case STAFF:
            return "vnd.android.cursor.dir/vnd.itnoles.staff";
        case STAFF_ID:
            return "vnd.android.cursor.item/vnd.itnoles.staff";
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        final int match = URIMATCHER.match(uri);
        switch (match) {
        case SCHEDULE:
            qb.setTables(SportsConstants.SCHEDULE);
            break;
        case SCHEDULE_ID:
            qb.setTables(SportsConstants.SCHEDULE);
            qb.appendWhereEscapeString(Schedule.SCHEDULE_ID + "=" + uri.getPathSegments().get(1) + "");
            break;
        case LINK:
            qb.setTables(SportsConstants.LINK);
            break;
        case LINK_ID:
            qb.setTables(SportsConstants.LINK);
            qb.appendWhereEscapeString(Link.LINK_ID + "=" + uri.getPathSegments().get(1) + "");
            break;
        case STAFF:
            qb.setTables(SportsConstants.STAFF);
            break;
        case STAFF_ID:
            qb.setTables(SportsConstants.STAFF);
            qb.appendWhereEscapeString(Staff.STAFF_ID + "=" + uri.getPathSegments().get(1) + "");
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        // Apply the query to the underlying database.
        final Cursor c = qb.query(mScheduleDB, projection, selection, selectionArgs, null, null, sortOrder);

        // Register the contexts ContentResolver to be notified if
        // the cursor result set changes.
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // Return a cursor to the query result.
        return c;
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final int match = URIMATCHER.match(uri);
        switch (match) {
        case SCHEDULE:
            mScheduleDB.insertOrThrow(SportsConstants.SCHEDULE, null, values);
            final Uri schUri = Schedule.buildScheduleUri(values.getAsString(Schedule.SCHEDULE_ID));
            getContext().getContentResolver().notifyChange(schUri, null);
            return schUri;
        case LINK:
            mScheduleDB.insertOrThrow(SportsConstants.LINK, null, values);
            final Uri lnkUri =  Link.buildLinkUri(values.getAsString(Schedule.SCHEDULE_ID));
            getContext().getContentResolver().notifyChange(lnkUri, null);
            return lnkUri;
        case STAFF:
            mScheduleDB.insertOrThrow(SportsConstants.STAFF, null, values);
            final Uri staffUri = Staff.buildStaffUri(values.getAsString(Schedule.SCHEDULE_ID));
            getContext().getContentResolver().notifyChange(staffUri, null);
            return staffUri;
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int count;
        final int match = URIMATCHER.match(uri);
        switch (match) {
        case SCHEDULE:
            count = mScheduleDB.update(SportsConstants.SCHEDULE, values, selection, selectionArgs);
            break;
        case SCHEDULE_ID:
            count = mScheduleDB.update(SportsConstants.SCHEDULE, values, Schedule.SCHEDULE_ID + "=" + uri.getPathSegments().get(1), selectionArgs);
            break;
        case LINK:
            count = mScheduleDB.update(SportsConstants.LINK, values, selection, selectionArgs);
            break;
        case LINK_ID:
            count = mScheduleDB.update(SportsConstants.LINK, values, Link.LINK_ID + "=" + uri.getPathSegments().get(1), selectionArgs);
            break;
        case STAFF:
            count = mScheduleDB.update(SportsConstants.STAFF, values, selection, selectionArgs);
            break;
        case STAFF_ID:
            count = mScheduleDB.update(SportsConstants.STAFF, values, Staff.STAFF_ID + "=" + uri.getPathSegments().get(1), selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

     /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int count;
        final int match = URIMATCHER.match(uri);
        switch (match) {
        case SCHEDULE:
            count = mScheduleDB.delete(SportsConstants.SCHEDULE, selection, selectionArgs);
            break;
        case SCHEDULE_ID:
            count = mScheduleDB.delete(SportsConstants.SCHEDULE, Schedule.SCHEDULE_ID + "='" + uri.getPathSegments().get(1) + "'", selectionArgs);
            break;
        case LINK:
            count = mScheduleDB.delete(SportsConstants.LINK, selection, selectionArgs);
            break;
        case LINK_ID:
            count = mScheduleDB.delete(SportsConstants.LINK, Link.LINK_ID + "='" + uri.getPathSegments().get(1) + "'", selectionArgs);
            break;
        case STAFF:
            count = mScheduleDB.delete(SportsConstants.STAFF, selection, selectionArgs);
            break;
        case STAFF_ID:
            count = mScheduleDB.delete(SportsConstants.STAFF, Staff.STAFF_ID + "='" + uri.getPathSegments().get(1) + "'", selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException
    {
        mScheduleDB.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            mScheduleDB.setTransactionSuccessful();
            return results;
        }
        finally {
            mScheduleDB.endTransaction();
        }
    }

    // Helper class for opening, creating, and managing database version control
    private static class ScheduleDatabaseHelper extends SQLiteOpenHelper
    {
        public ScheduleDatabaseHelper(Context context, String name, CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE schedule ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "schedule_id TEXT,"
                + "date TEXT,"
                + "time TEXT,"
                + "school TEXT,"
                + "updated TEXT,"
                + "UNIQUE (schedule_id) ON CONFLICT REPLACE)");

            db.execSQL("CREATE TABLE staff ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "staff_id TEXT,"
                + "name TEXT,"
                + "positions TEXT,"
                + "updated TEXT,"
                + "UNIQUE (staff_id) ON CONFLICT REPLACE)");

            db.execSQL("CREATE TABLE link ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "link_id TEXT,"
                + "name TEXT,"
                + "url TEXT,"
                + "updated TEXT,"
                + "UNIQUE (link_id) ON CONFLICT REPLACE)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            if (oldVersion != DATABASE_VERSION) {
                Log.w(TAG, "Destroying old data during upgrade");

                db.execSQL("DROP TABLE IF EXISTS schedule");
                db.execSQL("DROP TABLE IF EXISTS staff");
                db.execSQL("DROP TABLE IF EXISTS link");

                onCreate(db);
            }
        }
    }
}