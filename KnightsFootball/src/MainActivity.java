/*
 * Copyright (C) 2012 Jonathan Steele
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

import android.os.AsyncTask;
import android.os.Bundle;

import com.itnoles.shared.activities.AbstractMainActivity;
import com.itnoles.shared.io.RemoteExecutor;

public class MainActivity extends AbstractMainActivity {
    private static final String WORKSHEET_URL = "https://spreadsheets.google.com/feeds/worksheets/0AvRfIfyMiQAGdFowOThSZGs5OXpQMnpvdEJSc29TWHc/public/basic";

    TabsAdapter mTabsAdapter;

    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabsAdapter = new TabsAdapter(this);

        final Bundle headlines = new Bundle();
        headlines.putString("title", "Top Athletics Stories");
        headlines.putString("url", "http://www.ucfathletics.com/sports/m-footbl/headline-rss.xml");
        mTabsAdapter.addTab(bar.newTab().setText("News"), KnightsHeadlinesFragment.class, headlines);

        mTabsAdapter.addTab(bar.newTab().setText("Team"), TeamFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText("Link"), LinkFragment.class, null);

        // Load and parse the XML worksheet from Google Spreadsheet
        final AsyncTask<Void, Void, Void> doSyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final RemoteExecutor executor = new RemoteExecutor(MainActivity.this, getContentResolver());
                executor.executeWithPullParser(WORKSHEET_URL, new WorksheetsHandler(executor), 4096);
                return null;
            }
        };
        doSyncTask.execute();
    }
}