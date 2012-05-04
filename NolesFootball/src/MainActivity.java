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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.itnoles.shared.activities.AbstractMainActivity;
import com.itnoles.shared.fragments.TeamFragment;

public class MainActivity extends AbstractMainActivity {
    // Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        final TabsAdapter tabsAdapter = new TabsAdapter(this);
        tabsAdapter.addTab(bar.newTab().setText("News"), HeadlinesFragment.class);
        tabsAdapter.addTab(bar.newTab().setText("Team"), TeamFragment.class);
        tabsAdapter.addTab(bar.newTab().setText("Link"), LinkFragment.class);

        // This isn't directly affecting the UI, so put it on a worker thread.
        final AsyncTask<Void, Void, Void> doSyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final Intent syncIntent = new Intent(MainActivity.this, SyncService.class);
                startService(syncIntent);
                return null;
            }
        };
        doSyncTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            final Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}