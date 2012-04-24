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

package com.itnoles.knightfootball;

import android.os.Bundle;

import com.itnoles.shared.activities.AbstractSettingsActivity;

public class SettingsActivity extends AbstractSettingsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CharSequence[] entries = new CharSequence[] {
            "Top Athletics Stories", "UCF Sports", "UCF Pride", "O\'Leary Blogs", "Student Paper", "UCF Today", "Orlando Sentinel",
        };
        mNewsPref.setEntries(entries);

        final CharSequence[] entriesValue = new CharSequence[] {
            "http://www.ucfathletics.com/headline-rss.xml",
            "http://ucf.rivals.com/rss2feed.asp?SID=908",
            "http://feeds2.feedburner.com/sports/college/goldenknightsnotepad",
            "http://olearypsiphi.com/RSS/rss.php",
            "http://www.centralfloridafuture.com/se/central-florida-future-rss-1.991045",
            "http://today.ucf.edu/feed/",
            "http://www.orlandosentinel.com/sports/college/knights/rss2.0.xml",
        };
        mNewsPref.setEntryValues(entriesValue);

        final String getNewsURL = mNewsPref.getSharedPreferences().getString(SP_KEY_NEWS_URL, entriesValue[0].toString());
        mNewsPref.setValueIndex(mNewsPref.findIndexOfValue(getNewsURL));
        mNewsPref.setSummary(mNewsPref.getEntry());
    }
}