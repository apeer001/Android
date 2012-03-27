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
import android.util.Log;
import android.util.Xml;

import com.itnoles.shared.util.NetHttp;
import com.itnoles.shared.util.NetworkUtils;
import com.itnoles.shared.util.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsListLoader extends AsyncListLoader<News> {
    private static final String LOG_TAG = "NewsListLoader";

	private final String mURL;

    public NewsListLoader(Context context, String url) {
        super(context);
        this.mURL = url;
    }

    private ArrayList<News> parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        final ArrayList<News> results = new ArrayList<News>();

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

    /**
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<News> loadInBackground() {
        if (!NetworkUtils.isNetworkConnected(getContext())) {
            return null;
        }
        NetHttp http = null;
        try {
            http = new NetHttp(mURL);
            final XmlPullParser parser = Xml.newPullParser();
            parser.setInput(http.getInputStream(), null);
            return parse(parser);
        } catch (XmlPullParserException e) {
            Log.w(LOG_TAG, "Problem parsing XML response", e);
        } catch (IOException e) {
            Log.w(LOG_TAG, "Problem reading response", e);
        } finally {
            if (http != null) {
                http.close();
            }
        }
        return null;
    }
}