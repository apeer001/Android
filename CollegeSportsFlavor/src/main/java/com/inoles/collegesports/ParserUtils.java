/*
 * Copyright (c) 2013 Jonathan Steele
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.inoles.collegesports;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

class ParserUtils {
    private static final XmlPullParserFactory XPP_FACTORY;
    static {
        try {
            XPP_FACTORY = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(
                    "An exception occurred while calling XmlPullParserFactory.newInstance()." +
                    "A library providing the impl of the XML Pull Parser spec" +
                    "must be available at runtime.",
            e);
        }
    }

    private ParserUtils() {}

    /**
     * Build and return a new {@link XmlPullParser} with the given
     * {@link StringReader} assigned to it.
     */
    static XmlPullParser newPullParser(StringReader sr) throws XmlPullParserException {
        XmlPullParser parser = XPP_FACTORY.newPullParser();
        parser.setInput(sr);
        return parser;
    }
}
