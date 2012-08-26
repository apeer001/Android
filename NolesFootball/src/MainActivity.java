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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.itnoles.shared.Utils;
import com.itnoles.shared.activities.AbstractMainActivity;
import com.itnoles.shared.io.RemoteExecutor;

public class MainActivity extends AbstractMainActivity {
    private static final String WORKSHEET_URL = "https://spreadsheets.google.com/feeds/worksheets/0AvRfIfyMiQAGdDI4dEkwZW9XcDdqUHVOcXpzU0FqcWc/public/basic";

    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addViewPagerWithTab(new HomePagerAdapter(getSupportFragmentManager()));

        // Load and parse the XML worksheet from Google Spreadsheet
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

                final RemoteExecutor executor = new RemoteExecutor(MainActivity.this, getContentResolver());
                executor.executeWithPullParser(WORKSHEET_URL, new WorksheetsHandler(executor), 4096);
                return null;
            }
        };
        doSyncTask.execute();
    }

    private class HomePagerAdapter extends FragmentPagerAdapter {
        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    final Bundle bundle = new Bundle();
                    bundle.putString("title", "Top Athletics Stories");
                    bundle.putString("url", "http://www.seminoles.com/sports/m-footbl/headline-rss.xml");
                    return Fragment.instantiate(MainActivity.this, "NolesHeadlinesFragment", bundle);
                case 1:
                    return new TeamFragment();
                case 2:
                    return new LinkFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}