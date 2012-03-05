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

import android.util.Log;

import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.News;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class HeadlinesHandler {
    private static final String LOG_TAG = "HeadlinesHandler";

    private ArrayList<News> mList;

    private void parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        News currentNews = null;
        int type = parser.getEventType();
        while (type != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch(type) {
                case XmlPullParser.START_DOCUMENT:
                    mList = new ArrayList<News>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if ("item".equals(name) || SportsConstants.ENTRY.equals(name)) {
                        currentNews = new News();
                    } else if (currentNews != null) {
                        if (SportsConstants.LINK.equals(name) && parser.getAttributeCount() > 0) {
                            final String url = parser.getAttributeValue(null, "url");
                            currentNews.setValue(name, url);
                        } else {
                            currentNews.setValue(name, parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if ("item".equals(name) || SportsConstants.ENTRY.equals(name) && currentNews != null) {
                        mList.add(currentNews);
                        break;
                    }
                    break;
                default:
            }
            type = parser.next();
        }
    }

    public void parseXML(XmlPullParser parser) {
        try {
            parse(parser);
        } catch (XmlPullParserException e) {
            Log.w(LOG_TAG, "Problem parsing XML response", e);
        } catch (IOException e) {
            Log.w(LOG_TAG, "Problem reading response", e);
        }
    }

    public ArrayList<News> getNews() {
        return mList;
    }
}