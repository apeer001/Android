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

import com.itnoles.nolesfootball.io.model.Rosters;
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
 * A custom Loader that loads all of the rosters.
 */
public class RostersListLoader extends FeedListLoader<Rosters> {
    private static final String TAG = LogUtils.makeLogTag(RostersListLoader.class);

    public RostersListLoader(Context context) {
        super(context, "http://grfx.cstv.com/schools/fsu/data/xml/roster/m-footbl-2012.xml");
    }

    /**
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<Rosters> loadInBackground() {
        RostersList rostersList = new RostersList();
        HttpConnectionHelper connection = new HttpConnectionHelper(getContext());
        connection.execute(mURL, rostersList);
        return rostersList.getResults();
    }

    static class RostersList implements HttpConnectionHelper.HttpListener {
        private List<Rosters> mResults = Lists.newArrayList();

        public void onPostExecute(InputStreamReader is) throws IOException {
            try {
                // The Rosters that is currently being parsed
                Rosters currentRosters = null;

                XmlPullParser parser = XMLUtils.parseXML(is);
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    String name = parser.getName();
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                        if ("player".equals(name) || "asst_coach_lev1".equals(name) || "asst_coach_lev2".equals(name) || "asst_coach_lev3".equals(name)
                            || "head_coach".equals(name) || "other".equals(name)) {
                            currentRosters = new Rosters(!"player".equals(name));
                        } else if (currentRosters != null) {
                            currentRosters.setValue(name, parser.nextText());
                        }
                    } else if (parser.getEventType() == XmlPullParser.END_TAG) {
                        if ("player".equals(name) || "asst_coach_lev1".equals(name) || "asst_coach_lev2".equals(name) || "asst_coach_lev3".equals(name)
                            || "head_coach".equals(name) || "other".equals(name)) {
                            mResults.add(currentRosters);
                        }
                    }
                }
            } catch (XmlPullParserException e) {
                LogUtils.LOGW(TAG, "Malformed response for ", e);
            }
        }

        List<Rosters> getResults() {
            return mResults;
        }
    }
}