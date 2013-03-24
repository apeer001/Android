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
import com.itnoles.shared.io.model.Rosters;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.Collator;
import java.util.*;

/**
 * A custom Loader that loads all of the headlines.
 */
public class RostersListLoader extends FeedListLoader<Rosters> {
    public RostersListLoader(Context context, String schoolCode) {
        super(context, "http://grfx.cstv.com/schools/"+schoolCode+"/data/xml/roster/m-footbl-2012.xml");
    }

    /**
     * This is where the bulk of our work is done. This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<Rosters> loadInBackground() {
        final RostersList rostersList = new RostersList();
        final XMLParserConnection connection = new XMLParserConnection(getContext());
        connection.execute(mURL, rostersList);
        return rostersList.getResults();
    }

    /**
     * Perform boolean comparsions of rosters object where staff on bottom.
     */
    private static final Comparator<Rosters> STAFF_COMPARATOR = new Comparator<Rosters>() {
        @Override
        public int compare(Rosters object1, Rosters object2) {
            final boolean v1 = object1.getStaff();
            final boolean v2 = object2.getStaff();
            return (v1 ^ v2) ? ((v1 ^ false) ? 1 : -1) : 0;
        }
    };

    /**
     * Perform alphabetical comparison of rosters objects.
     */
    private static final Comparator<Rosters> ALPHA_COMPARATOR = new Comparator<Rosters>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(Rosters object1, Rosters object2) {
            return sCollator.compare(object1.getFullName(), object2.getFullName());
        }
    };

    static class RostersList implements XMLParserConnection.XMLParserListener {
        private List<Rosters> mResults = new ArrayList<Rosters>();

        public void onPostExecute(XmlPullParser parser) throws XmlPullParserException, IOException {
            // The Rosters that is currently being parsed
            Rosters currentRosters = null;

            // The current event returned by the parser
            int eventType;
            while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
                final String name = parser.getName();
                if (eventType == XmlPullParser.START_TAG) {
                    if ("player".equals(name) || "asst_coach_lev1".equals(name) || "asst_coach_lev2".equals(name) || "asst_coach_lev3".equals(name)
                        || "head_coach".equals(name) || "other".equals(name)) {
                        currentRosters = new Rosters();
                        currentRosters.setStaff(!"player".equals(name));
                    } else if (currentRosters != null) {
                        currentRosters.setValue(name, parser.nextText());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if ("player".equals(name) || "asst_coach_lev1".equals(name) || "asst_coach_lev2".equals(name) || "asst_coach_lev3".equals(name)
                        || "head_coach".equals(name) || "other".equals(name)) {
                        mResults.add(currentRosters);
                    }
                }
            }
        }

        List<Rosters> getResults() {
            // Sort the list.
            Collections.sort(mResults, ALPHA_COMPARATOR);
            Collections.sort(mResults, STAFF_COMPARATOR);

            // Done
            return mResults;
        }
    }
}