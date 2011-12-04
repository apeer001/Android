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

package com.itnoles.shared.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuInflater;
//import com.actionbarsherlock.view.MenuItem;
import com.itnoles.shared.R;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.PlatformSpecificImplementationFactory;
import com.itnoles.shared.util.base.IStrictMode;

public abstract class AbstractMainActivity extends FragmentActivity {
    final ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);
    protected ActionBar mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if (SportsConstants.DEVELOPER_MODE) {
            final IStrictMode strictMode = PlatformSpecificImplementationFactory.getStrictMode();
            if (strictMode != null) {
                strictMode.enableStrictMode();
            }
        }
		super.onCreate(savedInstanceState);
		mSherlock.setContentView(R.layout.fragment_layer);

		mActionBar = mSherlock.getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

    public void setActionBarSubtitle(String subtitle) {
        mActionBar.setSubtitle(subtitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            showSetting();
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void showSetting();

	/**
	 * A TabListener receives event callbacks from the action bar as tabs
	 * are deselected, selected, and reselected. A FragmentTransaction
	 * is provided to each of these callbacks; if any operations are added
	 * to it, it will be committed at the end of the full tab switch operation.
	 * This lets tab switches be atomic without the app needing to track
	 * the interactions between different tabs.
	 */
	public class TabListener implements ActionBar.TabListener {
		private Fragment mFragment;

		public TabListener(Fragment fragment) {
            mFragment = fragment;
        }

        public void onTabSelected(Tab tab) { //FragmentTransaction unused*/)
        	final FragmentManager fm = getSupportFragmentManager();
	        if (fm.getBackStackEntryCount() > 0) {
		        fm.popBackStack();
		    }
		    fm.beginTransaction().replace(R.id.titles, mFragment, null).commit();
		}

		public void onTabUnselected(Tab tab) { //FragmentTransaction unused)
			// do nothing
		}

		public void onTabReselected(Tab tab) { //FragmentTransaction unused)
			// do nothing
		}
	}
}