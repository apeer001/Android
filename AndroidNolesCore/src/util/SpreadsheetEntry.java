/*
 * Copyright 2012 Google Inc.
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

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

public class SpreadsheetEntry extends HashMap<String, String> {
    public static final long serialVersionUID = 1L;

    private static final Pattern CONTENT_PATTERN = Pattern.compile(
            "(?:^|, )([_a-zA-Z0-9]+): (.*?)(?=\\s*$|, [_a-zA-Z0-9]+: )", Pattern.DOTALL);

    private long mUpdated;
    public long getUpdated() { return mUpdated; }

    public static SpreadsheetEntry fromParser(XmlPullParser parser) throws XmlPullParserException, IOException {
        final int depth = parser.getDepth();
        final SpreadsheetEntry entry = new SpreadsheetEntry();

        String tag = null;
        int type;
        while (((type = parser.next()) != END_TAG || parser.getDepth() > depth) && type != END_DOCUMENT) {
            if (type == START_TAG) {
                tag = parser.getName();
            } else if (type == END_TAG) {
                tag = null;
            } else if (type == TEXT) {
                final String text = parser.getText();
                if ("updated".equals(tag)) {
                    entry.mUpdated = ParserUtils.parseTime(text);
                } else if ("title".equals(tag)) {
                    entry.put("title", text);
                } else if ("content".equals(tag)) {
                    final Matcher matcher = CONTENT_PATTERN.matcher(text);
                    while (matcher.find()) {
                        final String key = matcher.group(1);
                        final String value = matcher.group(2).trim();
                        entry.put(key, value);
                    }
                }
            }
        }
        return entry;
    }
}