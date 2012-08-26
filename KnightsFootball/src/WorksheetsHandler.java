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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.knightfootball;

import android.content.ContentResolver;
import android.content.ContentProviderOperation;
import android.net.Uri;

import com.itnoles.shared.io.RemoteExecutor;
import com.itnoles.shared.io.ScheduleHandler;
import com.itnoles.shared.io.StaffHandler;
import com.itnoles.shared.io.XmlHandler;
import com.itnoles.shared.util.ParserUtils;
import com.itnoles.shared.util.WorksheetEntry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.itnoles.shared.util.LogUtils.makeLogTag;
import static com.itnoles.shared.util.LogUtils.LOGD;
import static com.itnoles.shared.util.LogUtils.LOGW;

public class WorksheetsHandler extends XmlHandler {
    private static final String TAG = makeLogTag(WorksheetsHandler.class);

    private final RemoteExecutor mExecutor;

    public WorksheetsHandler(RemoteExecutor executor) {
        super(ScheduleProvider.CONTENT_AUTHORITY);
        mExecutor = executor;
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(XmlPullParser parser, ContentResolver resolver) throws XmlPullParserException, IOException {
        final HashMap<String, WorksheetEntry> sheets = new HashMap<String, WorksheetEntry>();

        // collecting all known spreadsheets
        parser.require(XmlPullParser.START_TAG, null, "feed");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            // Starts by looking for the entry tag
            if ("entry".equals(name)) {
                final WorksheetEntry entry = WorksheetEntry.fromParser(parser);
                LOGD(TAG, "found worksheet " + entry.toString());
                sheets.put(entry.getTitle(), entry);
            }
        }

        // consider updating each spreadsheet based on update timestamp
        considerUpdate(sheets, ScheduleProvider.SCHEDULE_TXT, ScheduleProvider.SCHEDULE_CONTENT_URI, resolver);
        considerUpdate(sheets, ScheduleProvider.STAFF_TXT, ScheduleProvider.STAFF_CONTENT_URI, resolver);

        return new ArrayList<ContentProviderOperation>();
    }

    private void considerUpdate(HashMap<String, WorksheetEntry> sheets, String sheetName, Uri targetDir, ContentResolver resolver) {
        final WorksheetEntry entry = sheets.get(sheetName);
        if (entry == null) {
            // Silently ignore missing spreadsheets to allow sync to continue.
            LOGW(TAG, "Missing '" + sheetName + "' worksheet data");
            return;
        }

        final long localUpdated = ParserUtils.queryDirUpdated(targetDir, resolver);
        final long serverUpdated = entry.getUpdated();
        LOGD(TAG, "considerUpdate() for " + entry.getTitle() + " found localUpdated=" + localUpdated + ", server=" + serverUpdated);
        if (localUpdated >= serverUpdated) {
            return;
        }

        final XmlHandler handler = createRemoteHandler(entry, targetDir);
        mExecutor.executeWithPullParser(entry.getListFeed(), handler, 8192);
    }

    private XmlHandler createRemoteHandler(WorksheetEntry entry, Uri targetDir) {
        final String title = entry.getTitle();
        if (ScheduleProvider.SCHEDULE_TXT.equals(title)) {
            return new ScheduleHandler(ScheduleProvider.CONTENT_AUTHORITY, targetDir);
        } else if (ScheduleProvider.STAFF_TXT.equals(title)) {
            return new StaffHandler(ScheduleProvider.CONTENT_AUTHORITY, targetDir);
        } else {
            throw new IllegalArgumentException("Unknown worksheet type");
        }
    }
}