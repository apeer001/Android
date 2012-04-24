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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for
 * {@link ScheduleProvider}.
 */
public class ScheduleDatabase extends SQLiteOpenHelper {
    // Used for debugging and logging
    private static final String LOG_TAG = "ScheduleDatabaseHelper";

    /**
     * The database that the provider uses as its underlying data store
     */
    private static final String DATABASE_NAME = "schedule.db";

    /**
     * The database version
     */
    private static final int DATABASE_VERSION = 1;

    public ScheduleDatabase(Context context) {
        // calls the super constructor, requesting the default cursor factory.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the underlying database with table name and column names taken from the
     * ScheduleContract class.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE schedule ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "schedule_id TEXT,"
            + "date TEXT,"
            + "time TEXT,"
            + "school TEXT,"
            + "location TEXT,"
            + "updated TEXT)");

        db.execSQL("CREATE TABLE staff ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "staff_id TEXT,"
            + "name TEXT,"
            + "positions TEXT,"
            + "updated TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != DATABASE_VERSION) {
            Log.w(LOG_TAG, "Destroying old data during upgrade");

            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS schedule");
            db.execSQL("DROP TABLE IF EXISTS staff");

            // Recreates the database with a new version
            onCreate(db);
        }
    }
}