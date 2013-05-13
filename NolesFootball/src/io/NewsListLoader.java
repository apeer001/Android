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

package com.itnoles.nolesfootball.io;

import android.content.Context;

import com.itnoles.nolesfootball.io.model.News;
import com.itnoles.nolesfootball.util.HttpConnectionHelper;
import com.itnoles.nolesfootball.util.Lists;
import com.itnoles.nolesfootball.util.LogUtils;
import com.itnoles.nolesfootball.util.XMLUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A custom Loader that loads all of the headlines.
 */
public class NewsListLoader extends FeedListLoader<News> {
    private static final String TAG = LogUtils.makeLogTag(NewsListLoader.class);

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
        NewsList newsList = new NewsList();
        HttpConnectionHelper connection = new HttpConnectionHelper(getContext());
        connection.execute(mURL, newsList);
        return newsList.getResults();
    }

    static class NewsList implements HttpConnectionHelper.HttpListener {
        private List<News> mResults = Lists.newArrayList();

        public void onPostExecute(InputStreamReader is) throws IOException {
            try {
                // The News that is currently being parsed
                News currentNews = null;

                XmlPullParser parser = XMLUtils.parseXML(is);
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    String name = parser.getName();
                    switch (parser.getEventType()) {
                        case XmlPullParser.START_TAG:
                            if ("item".equals(name) || "entry".equals(name)) {
                                currentNews = new News();
                            } else if (currentNews != null) {
                                if ("link".equals(name) && parser.getAttributeCount() > 0) {
                                    currentNews.setValue(name, parser.getAttributeValue(null, "url"));
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
            } catch (XmlPullParserException e) {
                LogUtils.LOGW(TAG, "Malformed response for ", e);
            }
        }

        List<News> getResults() {
            return mResults;
        }
    }
}