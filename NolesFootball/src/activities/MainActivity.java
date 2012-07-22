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

package com.itnoles.nolesfootball.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.ActionBar;
import com.itnoles.nolesfootball.WorksheetsHandler;
import com.itnoles.nolesfootball.fragment.HeadlinesFragment;
import com.itnoles.nolesfootball.fragment.LinkFragment;
import com.itnoles.nolesfootball.fragment.TeamFragment;
import com.itnoles.shared.Utils;
import com.itnoles.shared.activities.AbstractMainActivity;
import com.itnoles.shared.io.RemoteExecutor;

public class MainActivity extends AbstractMainActivity {
    private static final String WORKSHEET_URL = "https://spreadsheets.google.com/feeds/worksheets/0AvRfIfyMiQAGdDI4dEkwZW9XcDdqUHVOcXpzU0FqcWc/public/basic";

    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        final TabsAdapter tabsAdapter = new TabsAdapter(this);
        tabsAdapter.addTab(bar.newTab().setText("News"), HeadlinesFragment.class, createBundleForURLFromPrefs());
        tabsAdapter.addTab(bar.newTab().setText("Team"), TeamFragment.class, null);
        tabsAdapter.addTab(bar.newTab().setText("Link"), LinkFragment.class, null);

        // Load and parse the XML Spreadsheet from Google Drive
        final AsyncTask<Void, Void, Void> doSyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                /**
                 * Check to see if we are connected to a data or wifi network.
                 * if false, return early or execute XML
                 */
                if (!Utils.isOnline(MainActivity.this)) {
                    return null;
                }

                final RemoteExecutor executor = new RemoteExecutor(getContentResolver());
                executor.executeWithPullParser(WORKSHEET_URL, new WorksheetsHandler(executor), 4096);
                return null;
            }
        };
        doSyncTask.execute();
    }

    private Bundle createBundleForURLFromPrefs() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Bundle bundle = new Bundle();
        bundle.putString("url", prefs.getString("newsurl_preference", "http://www.seminoles.com/sports/m-footbl/headline-rss.xml"));
        return bundle;
    }
}