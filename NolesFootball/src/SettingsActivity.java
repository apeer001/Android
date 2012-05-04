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

package com.itnoles.nolesfootball;

import android.os.Bundle;

import com.itnoles.shared.activities.AbstractSettingsActivity;

public class SettingsActivity extends AbstractSettingsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CharSequence[] entries = {
            "Top Athletics Stories", "Warchant", "Noles Digest", "Tomahawk Nation", "Spirit Blog", "Orlando Sentinel",
        };
        mNewsPref.setEntries(entries);

        final CharSequence[] entriesValue = {
            "http://www.seminoles.com/headline-rss.xml",
            "http://floridastate.rivals.com/rss2feed.asp?SID=1061",
            "http://rss.scout.com/rss.aspx?sid=16",
            "http://feeds.feedburner.com/sportsblogs/tomahawknation.xml",
            "http://www.seminoles.com/blog/atom.xml",
            "http://www.orlandosentinel.com/sports/college/seminoles/rss2.0.xml",
        };
        mNewsPref.setEntryValues(entriesValue);

        final String getNewsURL = mNewsPref.getSharedPreferences().getString("newsurl_preference", entriesValue[0].toString());
        mNewsPref.setValueIndex(mNewsPref.findIndexOfValue(getNewsURL));
        mNewsPref.setSummary(mNewsPref.getEntry());
    }
}