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

package com.itnoles.shared.util;

import android.util.Log;

import com.itnoles.shared.util.base.HttpTransport;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

public final class XMLPullParserUtils {
	private static final String LOG_TAG = "XMLPullParserUtils";
    private static final XmlPullParserFactory FACTORY;
    static {
        try {
            FACTORY = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new AssertionError(e);
        }
    }

    private XMLPullParserUtils() {
    }

	public static void execute(HttpTransport transport, String url, XMLPullParserManager manager) {
		try {
            final HttpTransport.LowLevelHttpResponse response = transport.buildResponse(url);
            final InputStream input = response.execute();
            try {
                final XmlPullParser parser = FACTORY.newPullParser();
                parser.setInput(input, null);
                manager.onPostExecute(parser);
            } catch(XmlPullParserException e) {
                Log.w(LOG_TAG, "Malformed response", e);
            } finally {
                if (input != null) {
                    input.close();
                }
                response.disconnect();
            }
        } catch(IOException e) {
            Log.w(LOG_TAG, "Problem reading remote response", e);
        }
	}

	public interface XMLPullParserManager {
		void onPostExecute(XmlPullParser parser) throws XmlPullParserException, IOException;
	}
}