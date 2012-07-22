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
import android.content.UriMatcher;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.v4.database.DatabaseUtilsCompat;

import java.util.ArrayList;

public abstract class AbstractScheduleProvider extends ContentProvider {
    protected ScheduleDatabase mOpenHelper;

    protected static final String UNKNOWN_URI_LOG = "Unknown uri: ";

    public static final String SCHEDULE_TXT = "schedule";
    protected static final int SCHEDULE = 100;
    protected static final int SCHEDULE_ID = 101;

    public static final String STAFF_TXT = "staff";
    protected static final int STAFF = 200;
    protected static final int STAFF_ID = 201;

    protected static final UriMatcher URIMATCHER = new UriMatcher(UriMatcher.NO_MATCH);

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
            case STAFF:
                return "vnd.android.cursor.dir/vnd.itnoles.staff";
            case STAFF_ID:
                return "vnd.android.cursor.item/vnd.itnoles.staff";
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI_LOG + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        final int match = URIMATCHER.match(uri);
        switch(match) {
            case SCHEDULE:
                qb.setTables(SCHEDULE_TXT);
                break;
            case SCHEDULE_ID:
                qb.setTables(SCHEDULE_TXT);
                qb.appendWhereEscapeString("date=" + uri.getLastPathSegment());
                break;
            case STAFF:
                qb.setTables(STAFF_TXT);
                break;
            case STAFF_ID:
                qb.setTables(STAFF_TXT);
                qb.appendWhereEscapeString("name=" + uri.getLastPathSegment());
                break;
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI_LOG + uri);
        }

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalSelection;
        int count;
        final int match = URIMATCHER.match(uri);
        switch(match) {
            case SCHEDULE:
                count = db.update(SCHEDULE_TXT, values, selection, selectionArgs);
                break;
            case SCHEDULE_ID:
                finalSelection = getIDFromUriWithSelectionArgs("date='" + uri.getLastPathSegment() + "'", selection);
                count = db.update(SCHEDULE_TXT, values, finalSelection, selectionArgs);
                break;
            case STAFF:
                count = db.update(STAFF_TXT, values, selection, selectionArgs);
                break;
            case STAFF_ID:
                finalSelection = getIDFromUriWithSelectionArgs("name='" + uri.getLastPathSegment() + "'", selection);
                count = db.update(STAFF_TXT, values, finalSelection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI_LOG + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalSelection;
        int count;
        final int match = URIMATCHER.match(uri);
        switch(match) {
            case SCHEDULE:
                count = db.delete(SCHEDULE_TXT, selection, selectionArgs);
                break;
            case SCHEDULE_ID:
                finalSelection = getIDFromUriWithSelectionArgs("date= '" + uri.getLastPathSegment() + "'", selection);
                count = db.delete(SCHEDULE_TXT, finalSelection, selectionArgs);
                break;
            case STAFF:
                count = db.delete(STAFF_TXT, selection, selectionArgs);
                break;
            case STAFF_ID:
                finalSelection = getIDFromUriWithSelectionArgs("name= '" + uri.getLastPathSegment() + "'", selection);
                count = db.delete(STAFF_TXT, finalSelection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI_LOG + uri);
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

    private String getIDFromUriWithSelectionArgs(String column, String selection) {
        return DatabaseUtilsCompat.concatenateWhere(column, selection);
    }
}