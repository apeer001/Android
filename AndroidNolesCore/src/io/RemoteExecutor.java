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

import com.itnoles.shared.util.XMLPullParserUtils;
import com.itnoles.shared.util.base.HttpTransport;

import org.xmlpull.v1.XmlPullParser;

public class RemoteExecutor {
    private final HttpTransport mTransport;
    private final ContentResolver mResolver;

    public RemoteExecutor(HttpTransport transport, ContentResolver resolver) {
        mTransport = transport;
        mResolver = resolver;
    }

    public void executeWithPullParser(String url, final XmlHandler handler) {
        XMLPullParserUtils.execute(mTransport, url, new XMLPullParserUtils.XMLPullParserManager() {
            public void onPostExecute(XmlPullParser parser) {
                handler.parseAndApply(parser, mResolver);
            }
        });
    }
}