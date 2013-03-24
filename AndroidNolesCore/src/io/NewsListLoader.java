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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.shared.io;

import android.content.Context;

import com.itnoles.shared.XMLParserConnection;
import com.itnoles.shared.io.model.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.*;

/**
 * A custom Loader that loads all of the headlines.
 */
public class NewsListLoader extends FeedListLoader<News> {
    public NewsListLoader(Context context, String url) {
        super(context, url);
    }

    /**
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<News> loadInBackground() {
        final NewsList newsList = new NewsList();
        final XMLParserConnection connection = new XMLParserConnection(getContext());
        connection.execute(mURL, newsList);
        return newsList.getResults();
    }

    static class NewsList implements XMLParserConnection.XMLParserListener {
        private List<News> mResults = new ArrayList<News>();

        public void onPostExecute(XmlPullParser parser) throws XmlPullParserException, IOException {
            // The News that is currently being parsed
            News currentNews = null;

            // The current event returned by the parser
            int eventType;
            while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
                final String name = parser.getName();
                switch(eventType) {
                    case XmlPullParser.START_TAG:
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
                        break;
                    case XmlPullParser.END_TAG:
                        if ("item".equals(name) || "entry".equals(name)) {
                            mResults.add(currentNews);
                        }
                        break;
                    default:
                }
            }
        }

        List<News> getResults() {
            return mResults;
        }
    }
}