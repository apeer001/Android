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

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.provider.ScheduleContract.Staff;
import com.itnoles.shared.util.Lists;
import com.itnoles.shared.util.SpreadsheetEntry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import static com.itnoles.shared.util.ParserUtils.queryItemDetails;
import static com.itnoles.shared.util.ParserUtils.sanitizeId;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class StaffHandler extends XmlHandler
{
    private static final String TAG = "StaffHandler";

    @Override
    public ArrayList<ContentProviderOperation> parse(XmlPullParser parser, ContentResolver resolver) throws XmlPullParserException, IOException
    {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        // Walk document, parsing any incoming entries
        int type;
        while ((type = parser.next()) != END_DOCUMENT) {
            if (type == START_TAG && SportsConstants.ENTRY.equals(parser.getName())) {
                // Process single spreadsheet row at a time
                final SpreadsheetEntry entry = SpreadsheetEntry.fromParser(parser);
                final String staffId = sanitizeId(entry.get("title"));
                final Uri staffUri = Staff.buildStaffUri(staffId);

                // Check for existing details, only update when changed
                final long localUpdated = queryItemDetails(staffUri, resolver);
                final long serverUpdated = entry.getUpdated();
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "found staff " + entry.toString());
                    Log.v(TAG, "found localUpdated=" + localUpdated + ", server=" + serverUpdated);
                }
                if (localUpdated >= serverUpdated) {
                    continue;
                }

                // Clear any existing values for this staff, treating the
                // incoming details as authoritative.
                batch.add(ContentProviderOperation.newDelete(staffUri).build());

                final ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Staff.CONTENT_URI);
                builder.withValue(SportsConstants.UPDATED, serverUpdated);
                builder.withValue(Staff.STAFF_ID, staffId);
                builder.withValue(Staff.NAME, entry.get("title"));
                builder.withValue(Staff.POSITIONS, entry.get(Staff.POSITIONS));

                // Normal staff details ready, write to provider
                batch.add(builder.build());
            }
        }
        return batch;
    }
}