/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itnoles.shared.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;

import com.itnoles.shared.R;
import com.itnoles.shared.service.SyncService;

public class MainActivity extends FragmentActivity
{
    private int mThemeId = -1;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getInt("theme", -1) != -1)
		{
		    mThemeId = savedInstanceState.getInt("theme");
            setTheme(mThemeId);
        }

		setContentView(R.layout.fragment_layer);

		final ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		final HeadlinesFragment headlines = new HeadlinesFragment();
		bar.addTab(bar.newTab().setText("Headlines").setTabListener(new TabListener(headlines)));

		final ScheduleFragment schedule = new ScheduleFragment();
		bar.addTab(bar.newTab().setText("Schedule").setTabListener(new TabListener(schedule)));

		final LinkFragment link = new LinkFragment();
		bar.addTab(bar.newTab().setText("Link").setTabListener(new TabListener(link)));

		final StaffFragment staff = new StaffFragment();
		bar.addTab(bar.newTab().setText("Staff").setTabListener(new TabListener(staff)));

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tabpos"));
		}

	    final Intent syncIntent = new Intent(this, SyncService.class);
	    startService(syncIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	    menu.add(Menu.NONE, R.string.daynight, Menu.NONE, R.string.daynight);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) {
        case R.string.daynight:
            if (mThemeId == R.style.Theme_Sherlock || mThemeId == -1) {
                mThemeId = R.style.Theme_Sherlock_Light;
            }
            else {
                mThemeId = R.style.Theme_Sherlock;
            }
            recreate();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("tabpos", getSupportActionBar().getSelectedNavigationIndex());
        outState.putInt("theme", mThemeId);
    }

	/**
     * A TabListener receives event callbacks from the action bar as tabs
     * are deselected, selected, and reselected. A FragmentTransaction
     * is provided to each of these callbacks; if any operations are added
     * to it, it will be committed at the end of the full tab switch operation.
     * This lets tab switches be atomic without the app needing to track
     * the interactions between different tabs.
     */
    private class TabListener implements ActionBar.TabListener
    {
        private Fragment mFragment;

        public TabListener(Fragment fragment)
        {
            mFragment = fragment;
        }

        public void onTabSelected(Tab tab, FragmentTransaction unused)
        {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.titles, mFragment, null)
                .commit();
        }

        public void onTabUnselected(Tab tab, FragmentTransaction unused)
        {
    	       // do nothing
        }

        public void onTabReselected(Tab tab, FragmentTransaction unused)
        {
    	       // do nothing
        }
    }
}