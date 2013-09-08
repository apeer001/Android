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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.flavored;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class XMLContentLoader<T> extends AbstractContentListLoader<T> {
    private static final String LOG_TAG = "XmlContentLoader";

    private final ResponseListener<T> mListener;

    private static XmlPullParserFactory sXmlPullParserFactory;
    static {
        try {
            sXmlPullParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "Could not instantiate XmlPullParserFactory", e);
        }
    }

    public XMLContentLoader(Context context, String url, ResponseListener<T> listener) {
        super(context, url);
        mListener = listener;
    }

    @Override
    public List<T> loadInBackground() {
        InputStreamReader reader = null;
        try {
            reader = Utils.openUrlConnection(mURL);
            XmlPullParser parser = sXmlPullParserFactory.newPullParser();
            parser.setInput(reader);
            return mListener.onPostExecute(parser);
        } catch (XmlPullParserException xppe) {
            Log.w(LOG_TAG, "Problem on parsing xml file", xppe);
        } catch (IOException ioe) {
            Log.w(LOG_TAG, "Problem on xml file", ioe);
        } finally {
            Utils.ignoreQuietly(reader);
        }

        return null;
    }

    public interface ResponseListener<T> {
        List<T> onPostExecute(XmlPullParser parser) throws IOException, XmlPullParserException;
    }
}