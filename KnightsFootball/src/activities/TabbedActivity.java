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

package com.itnoles.knightfootball.activities;

import android.content.Intent;
import android.os.Bundle;

import com.itnoles.knightfootball.fragments.HeadlinesFragment;
import com.itnoles.knightfootball.service.SyncService;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.activities.AbstractTabbedActivity;
import com.itnoles.shared.fragments.LinkFragment;
import com.itnoles.shared.fragments.TeamFragment;

public class TabbedActivity extends AbstractTabbedActivity {
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final HeadlinesFragment headlines = new HeadlinesFragment();
		mActionBar.addTab(mActionBar.newTab().setText("Headlines").setTabListener(new TabListener(headlines)));

		final TeamFragment team = new TeamFragment();
		mActionBar.addTab(mActionBar.newTab().setText("Team").setTabListener(new TabListener(team)));

		final LinkFragment link = new LinkFragment();
		mActionBar.addTab(mActionBar.newTab().setText("Link").setTabListener(new TabListener(link)));

        final Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);   
    }

    @Override
    protected void showSetting() {
        final Class preferenceClass = SportsConstants.SUPPORTS_HONEYCOMB ? ModernSettingActivity.class : LegacySettingsActivity.class;
        final Intent intent = new Intent(this, preferenceClass);
        startActivity(intent);
    }
}