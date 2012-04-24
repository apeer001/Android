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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.Xml;

import com.itnoles.shared.util.HttpUtils;
import com.itnoles.shared.util.News;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewsListLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = "NewsListLoader";

    private final String mURL;
    private final HttpClient mHttpClient;

    private List<News> mList;

    public NewsListLoader(Context context, String url) {
        super(context);
        this.mURL = url;
        this.mHttpClient = HttpUtils.getHttpClient(context);
    }

    /**
     * Called when there is new data to deliver to the client. The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<News> list) {
        mList = list;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(list);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mList);
        }

        if (mList == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<News> loadInBackground() {
        final ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (!(activeNetwork != null && activeNetwork.isConnectedOrConnecting())) {
            return null;
        }

        final HttpGet request = new HttpGet(mURL);
        try {
            final HttpResponse response = mHttpClient.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(LOG_TAG, "Unexpected server response " + response.getStatusLine() + " for " + request.getRequestLine());
                return null;
            }
            final InputStream input = response.getEntity().getContent();
            try {
                final XmlPullParser parser = Xml.newPullParser();
                parser.setInput(input, null);
                return parse(parser);
            } catch (XmlPullParserException e) {
                Log.w(LOG_TAG, "Malformed response for " + request.getRequestLine(), e);
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        } catch (IOException e) {
            Log.w("Problem reading remote response for " + request.getRequestLine(), e);
        }
        return null;
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

        if (mList != null) {
            mList.clear();
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