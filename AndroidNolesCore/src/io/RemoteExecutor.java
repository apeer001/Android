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

import android.content.ContentResolver;
import android.util.Log;

import com.itnoles.shared.util.ParserUtils;
import com.itnoles.shared.util.base.HttpTransport;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class RemoteExecutor
{
    private static final String LOG_TAG = "RemoteExecutor";

    private HttpTransport mTransport;
    private final ContentResolver mResolver;

    public RemoteExecutor(HttpTransport transport, ContentResolver resolver)
    {
        mTransport = transport;
        mResolver = resolver;
    }

    public void executeWithPullParser(String url, XmlHandler handler)
    {
        try {
            final HttpTransport.LowLevelHttpResponse response = mTransport.buildResponse(url);
            final InputStream input = response.execute();
            try {
                final XmlPullParser parser = ParserUtils.newPullParser(input);
                handler.parseAndApply(parser, mResolver);
            }
            catch (XmlPullParserException e) {
                Log.w(LOG_TAG, "Malformed response", e);
            }
            finally {
                if (input != null) {
                    input.close();
                }
                response.disconnect();
            }
        }
        catch (IOException e) {
            Log.w(LOG_TAG, "Problem reading remote response", e);
        }
    }
}