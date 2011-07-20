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

package com.itnoles.shared.io;

import android.content.ContentResolver;
import android.content.ContentProviderOperation;
import android.net.Uri;
import android.util.Log;

import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.provider.ScheduleContract.Schedule;
import com.itnoles.shared.provider.ScheduleContract.Link;
import com.itnoles.shared.provider.ScheduleContract.Staff;
import com.itnoles.shared.util.Lists;
import com.itnoles.shared.util.Maps;
import com.itnoles.shared.util.ParserUtils;
import com.itnoles.shared.util.WorksheetEntry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class WorksheetsHandler extends XmlHandler
{
    private static final String TAG = "WorksheetsHandler";

    private RemoteExecutor mExecutor;

    public WorksheetsHandler(RemoteExecutor executor)
    {
        mExecutor = executor;
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(XmlPullParser parser, ContentResolver resolver) throws XmlPullParserException, IOException
    {
        final HashMap<String, WorksheetEntry> sheets = Maps.newHashMap();

        // walk response, collecting all known spreadsheets
        int type;
        while ((type = parser.next()) != END_DOCUMENT) {
            if (type == START_TAG && "entry".equals(parser.getName())) {
                final WorksheetEntry entry = WorksheetEntry.fromParser(parser);
                Log.d(TAG, "found worksheet " + entry.toString());
                sheets.put(entry.getTitle(), entry);
            }
        }

        // consider updating each spreadsheet based on update timestamp
        considerUpdate(sheets, SportsConstants.SCHEDULE, Schedule.CONTENT_URI, resolver);
        considerUpdate(sheets, SportsConstants.LINK, Link.CONTENT_URI, resolver);
        considerUpdate(sheets, SportsConstants.STAFF, Staff.CONTENT_URI, resolver);
        return Lists.newArrayList();
    }

    private void considerUpdate(HashMap<String, WorksheetEntry> sheets, String sheetName, Uri targetDir, ContentResolver resolver)
    {
        final WorksheetEntry entry = sheets.get(sheetName);
        if (entry == null) {
            Log.w(TAG, "Missing '" + sheetName + "' worksheet data");
            return;
        }

        final long localUpdated = ParserUtils.queryDirUpdated(targetDir, resolver);
        final long serverUpdated = entry.getUpdated();
        Log.d(TAG, "considerUpdate() for " + entry.getTitle() + " found localUpdated=" + localUpdated + ", server=" + serverUpdated);
        if (localUpdated >= serverUpdated) {
            return;
        }

        final XmlHandler handler = createRemoteHandler(entry);
        if (handler != null) {
            final String request = entry.getListFeed();
            mExecutor.executeWithPullParser(request, handler);
        }
    }

    private XmlHandler createRemoteHandler(WorksheetEntry entry)
    {
        final String title = entry.getTitle();
        if (SportsConstants.LINK.equals(title)) {
            return new LinkHandler();
        }
        else if (SportsConstants.STAFF.equals(title)) {
            return new StaffHandler();
        }
        return null;
    }
}