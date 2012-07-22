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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.shared.io;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Xml;

import com.itnoles.shared.Utils;
import com.itnoles.shared.util.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.*;
import java.util.*;

import static com.itnoles.shared.util.LogUtils.makeLogTag;
import static com.itnoles.shared.util.LogUtils.LOGW;

/**
 * A custom Loader that loads all of the headlines.
 */
public class NewsListLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = makeLogTag(NewsListLoader.class);

    private List<News> mNews;

    private final String mURL;

    public NewsListLoader(Context context, String url) {
        super(context);
        mURL = url;
    }

    /**
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<News> loadInBackground() {
        if (!Utils.isOnline(getContext())) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        try {
            final URL url = new URL(mURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            final InputStream input = new BufferedInputStream(urlConnection.getInputStream(), 8192);
            try {
                final XmlPullParser parser = Xml.newPullParser();
                parser.setInput(input, null);
                return parse(parser);
            } catch (XmlPullParserException e) {
                LOGW(LOG_TAG, "Malformed response for ", e);
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        } catch (IOException e) {
            LOGW(LOG_TAG, "Problem reading remote response for ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * Called when there is new data to deliver to the client. The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<News> news) {
        mNews = news;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(news);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mNews != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mNews);
        }

        if (mNews == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'news'
        // if needed.
        if (mNews != null) {
            mNews.clear();
        }
    }

    private List<News> parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        final List<News> results = new ArrayList<News>();
        // The News that is currently being parsed
        News currentNews = null;
        // The current event returned by the parser
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            if (eventType == XmlPullParser.START_TAG) {
                name = parser.getName();
                if ("item".equals(name) || "entry".equals(name)) {
                    currentNews = new News();
                } else if (currentNews != null) {
                    if ("link".equals(name) && parser.getAttributeCount() > 0) {
                        final String url = parser.getAttributeValue(null, "url");
                        currentNews.setValue(name, url);
                    } else {
                        currentNews.setValue(name, parser.nextText());
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                name = parser.getName();
                if ("item".equals(name) || "entry".equals(name)) {
                    results.add(currentNews);
                }
            }
            eventType = parser.next();
        }
        return results;
    }
}