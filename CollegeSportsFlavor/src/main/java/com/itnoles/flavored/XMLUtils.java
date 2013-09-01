/*
 * Copyright (C) 2013 Jonathan Steele
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

package com.itnoles.flavored;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;

public class XMLUtils {
    private static final String TAG = "XMLUtils";

    private static XmlPullParserFactory sXmlPullParserFactory;
    static {
        try {
            sXmlPullParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Could not instantiate XmlPullParserFactory", e);
        }
    }

    private XMLUtils() {}

    public static XmlPullParser parseXML(InputStreamReader is) throws XmlPullParserException {
        XmlPullParser parser = sXmlPullParserFactory.newPullParser();
        parser.setInput(is);
        return parser;
    }
}