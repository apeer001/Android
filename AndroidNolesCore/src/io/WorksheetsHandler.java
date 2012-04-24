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

package com.itnoles.shared.io;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.itnoles.shared.provider.ScheduleProvider;
import com.itnoles.shared.util.ParserUtils;
import com.itnoles.shared.util.WorksheetEntry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class WorksheetsHandler extends XmlHandler {
    private static final String TAG = "WorksheetsHandler";
    private static final String SCHEDULE = "schedule";
    private static final String STAFF = "staff";

    private final RemoteExecutor mExecutor;

    public WorksheetsHandler(RemoteExecutor executor) {
        super(ScheduleProvider.CONTENT_AUTHORITY);
        mExecutor = executor;
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(XmlPullParser parser, ContentResolver resolver) throws XmlPullParserException, IOException {
        final HashMap<String, WorksheetEntry> sheets = new HashMap<String, WorksheetEntry>();

        // walk response, collecting all known spreadsheets
        int type;
        while ((type = parser.next()) != END_DOCUMENT) {
            if (type == START_TAG && ENTRY.equals(parser.getName())) {
                final WorksheetEntry entry = WorksheetEntry.fromParser(parser);
                Log.d(TAG, "found worksheet " + entry.toString());
                sheets.put(entry.getTitle(), entry);
            }
        }

        // consider updating each spreadsheet based on update timestamp
        considerUpdate(sheets, SCHEDULE, ScheduleProvider.SCHEDULE_CONTENT_URI, resolver);
        considerUpdate(sheets, STAFF, ScheduleProvider.STAFF_CONTENT_URI, resolver);

        return new ArrayList<ContentProviderOperation>();
    }

    private void considerUpdate(HashMap<String, WorksheetEntry> sheets, String sheetName, Uri targetDir, ContentResolver resolver) {
        final WorksheetEntry entry = sheets.get(sheetName);
        if (entry == null) {
            // Silently ignore missing spreadsheets to allow sync to continue.
            Log.w(TAG, "Missing '" + sheetName + "' worksheet data");
            return;
        }

        final long localUpdated = ParserUtils.queryDirUpdated(targetDir, resolver);
        final long serverUpdated = entry.getUpdated();
        Log.d(TAG, "considerUpdate() for " + entry.getTitle() + " found localUpdated="
                + localUpdated + ", server=" + serverUpdated);
        if (localUpdated >= serverUpdated) {
            return;
        }

        final XmlHandler handler = createRemoteHandler(entry);
        mExecutor.executeWithPullParser(entry.getListFeed(), handler);
    }

    private XmlHandler createRemoteHandler(WorksheetEntry entry) {
        final String title = entry.getTitle();
        if (SCHEDULE.equals(title)) {
            return new ScheduleHandler();
        } else if (STAFF.equals(title)) {
            return new StaffHandler();
        } else {
            throw new IllegalArgumentException("Unknown worksheet type");
        }
    }
}