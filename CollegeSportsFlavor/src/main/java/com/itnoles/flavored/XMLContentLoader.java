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

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class XMLContentLoader<T> extends AsyncTaskLoader<List<T>> {
    private static final String LOG_TAG = "XmlContentLoader";

    private final ResponseListener<T> mListener;
    private final String mURL;

    private List<T> mResults;

    private static XmlPullParserFactory sXmlPullParserFactory;
    static {
        try {
            sXmlPullParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "Could not instantiate XmlPullParserFactory", e);
        }
    }

    public XMLContentLoader(Context context, String url, ResponseListener<T> listener) {
        super(context);
        mListener = listener;
        mURL = url;
    }

    @Override
    public List<T> loadInBackground() {
        if (mResults == null) {
            mResults = new ArrayList<>();
        }

        InputStreamReader reader = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(mURL).openConnection();
            reader = new InputStreamReader(connection.getInputStream());
            XmlPullParser parser = sXmlPullParserFactory.newPullParser();
            parser.setInput(reader);
            mListener.onPostExecute(parser, mResults);
        } catch (IOException | XmlPullParserException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch(IOException ignored) {}
        }

        return mResults;
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        cancelLoad();

        // At this point we can release the resources associated with 'result'
        // if needed.
        if (mResults != null) {
            mResults = null;
        }
    }

    public interface ResponseListener<T> {
       void onPostExecute(XmlPullParser parser, List<T> results) throws IOException, XmlPullParserException;
    }
}