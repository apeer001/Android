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