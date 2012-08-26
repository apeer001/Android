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

package com.itnoles.shared.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.format.DateUtils;

import java.io.IOException;

public class WorksheetEntry {
    private static final String REL_LISTFEED = "http://schemas.google.com/spreadsheets/2006#listfeed";

    private long mUpdated;
    private String mTitle;
    private String mListFeed;

    public long getUpdated() {
        return mUpdated;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getListFeed() {
        return mListFeed;
    }

    @Override
    public String toString() {
        return "title=" + mTitle + ", updated=" + mUpdated + " (" + DateUtils.getRelativeTimeSpanString(mUpdated) + ")";
    }

    public static WorksheetEntry fromParser(XmlPullParser parser) throws XmlPullParserException, IOException {
        final int depth = parser.getDepth();
        final WorksheetEntry entry = new WorksheetEntry();
        parser.require(XmlPullParser.START_TAG, null, "entry");
        while (parser.next() != XmlPullParser.END_DOCUMENT && parser.getDepth() > depth) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            if ("link".equals(name)) {
                final String relType = parser.getAttributeValue(null, "rel");
                if (REL_LISTFEED.equals(relType)) {
                    entry.mListFeed = parser.getAttributeValue(null, "href");
                }
            } else if ("title".equals(name)) {
                entry.mTitle = ParserUtils.readTitle(parser);
            } else if ("updated".equals(name)) {
                entry.mUpdated = ParserUtils.readUpdated(parser);
            }
        }
        return entry;
    }
}