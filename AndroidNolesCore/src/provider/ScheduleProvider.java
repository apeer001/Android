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
import android.net.Uri;

import com.itnoles.shared.provider.ScheduleContract.Schedule;
import com.itnoles.shared.provider.ScheduleContract.Link;
import com.itnoles.shared.provider.ScheduleContract.Staff;
import com.itnoles.shared.util.SelectionBuilder;

import java.util.ArrayList;

public class ScheduleProvider extends ContentProvider {
    private ScheduleDatabase mOpenHelper;

    private static final int SCHEDULE = 100;
    private static final int SCHEDULE_ID = 101;

    private static final int LINK = 200;
    private static final int LINK_ID = 201;

    private static final int STAFF = 300;
    private static final int STAFF_ID = 301;

    /**
     * Allocate the UriMatcher object that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    private static final UriMatcher URIMATCHER;
    static {
        URIMATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URIMATCHER.addURI(ScheduleContract.CONTENT_AUTHORITY, "schedule", SCHEDULE);
        URIMATCHER.addURI(ScheduleContract.CONTENT_AUTHORITY, "schedule/*", SCHEDULE_ID);

        URIMATCHER.addURI(ScheduleContract.CONTENT_AUTHORITY, "link", LINK);
        URIMATCHER.addURI(ScheduleContract.CONTENT_AUTHORITY, "link/*", LINK_ID);

        URIMATCHER.addURI(ScheduleContract.CONTENT_AUTHORITY, "staff", STAFF);
        URIMATCHER.addURI(ScheduleContract.CONTENT_AUTHORITY, "staff/*", STAFF_ID);
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new ScheduleDatabase(context);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getType(Uri uri) {
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URIMATCHER.match(uri);
        switch (match) {
            case SCHEDULE:
                final long schID = db.insertOrThrow(ScheduleContract.SCHEDULE, null, values);
                final Uri schUri = Schedule.buildScheduleUri(Long.toString(schID));
                getContext().getContentResolver().notifyChange(schUri, null);
                return schUri;
            case LINK:
                final long lnkID = db.insertOrThrow(ScheduleContract.LINK, null, values);
                final Uri lnkUri = Link.buildLinkUri(Long.toString(lnkID));
                getContext().getContentResolver().notifyChange(lnkUri, null);
                return lnkUri;
            case STAFF:
                final long staffID = db.insertOrThrow(ScheduleContract.STAFF, null, values);
                final Uri staffUri = Staff.buildStaffUri(Long.toString(staffID));
                getContext().getContentResolver().notifyChange(staffUri, null);
                return staffUri;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        final int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        final int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #query},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = URIMATCHER.match(uri);
        switch(match) {
            case SCHEDULE:
                return builder.table(ScheduleContract.SCHEDULE);
            case SCHEDULE_ID:
                return builder.table(ScheduleContract.SCHEDULE).where(Schedule.DATE + "='" + uri.getPathSegments().get(1) + "'");
            case LINK:
                return builder.table(ScheduleContract.LINK);
            case LINK_ID:
                return builder.table(ScheduleContract.LINK).where(Link.NAME + "='" + uri.getPathSegments().get(1) + "'");
            case STAFF:
                return builder.table(ScheduleContract.STAFF);
            case STAFF_ID:
                return builder.table(ScheduleContract.STAFF).where(Staff.NAME + "='" + uri.getPathSegments().get(1) + "'");
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}