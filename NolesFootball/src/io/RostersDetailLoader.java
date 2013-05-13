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
 * A custom Loader that loads all of the rosters detail items.
 */
public class RostersDetailLoader extends FeedListLoader<String> {
    private static final String TAG = LogUtils.makeLogTag(RostersDetailLoader.class);

    public RostersDetailLoader(Context context, String url) {
        super(context, url);
    }

    /**
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<String> loadInBackground() {
        RostersDetail detail = new RostersDetail();
        HttpConnectionHelper connection = new HttpConnectionHelper(getContext());
        connection.execute(mURL, detail);
        return detail.getResults();
    }

    static class RostersDetail implements HttpConnectionHelper.HttpListener {
        private List<String> mResults = Lists.newArrayList();

        public void onPostExecute(InputStreamReader is) throws IOException {
            try {
                XmlPullParser parser = XMLUtils.parseXML(is);
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                        String name = parser.getName();
                        if ("experience".equals(name)) {
                            mResults.add("Experience: " + parser.nextText());
                        } else if ("eligibility".equals(name)) {
                            mResults.add("Class: " + parser.nextText());
                        } else if ("height".equals(name)) {
                            mResults.add("Height: " + parser.nextText());
                        } else if ("weight".equals(name)) {
                            mResults.add("Weight: " + parser.nextText());
                        } else if ("hometown".equals(name)) {
                            mResults.add("Hometown: " + parser.nextText());
                        }
                    }
                }
            } catch (XmlPullParserException e) {
                LogUtils.LOGW(TAG, "Malformed response for ", e);
            }
        }

        List<String> getResults() {
            return mResults;
        }
    }
}