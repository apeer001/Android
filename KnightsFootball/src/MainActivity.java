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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.itnoles.shared.activities.AbstractMainActivity;
import com.itnoles.shared.fragments.TeamFragment;

public class MainActivity extends AbstractMainActivity {
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        final ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        final TabsAdapter tabsAdapter = new TabsAdapter(this, viewPager);
        tabsAdapter.addTab(bar.newTab().setText("News"), HeadlinesFragment.class);
        tabsAdapter.addTab(bar.newTab().setText("Team"), TeamFragment.class);
        tabsAdapter.addTab(bar.newTab().setText("Link"), LinkFragment.class);

        final Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            final Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}