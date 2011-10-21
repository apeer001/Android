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

package com.itnoles.shared.util;

import android.support.v4.app.ActionBar.Tab;
import android.support.v4.app.ActionBar.TabListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.itnoles.shared.R;

/**
 * A TabListener receives event callbacks from the action bar as tabs
 * are deselected, selected, and reselected. A FragmentTransaction
 * is provided to each of these callbacks; if any operations are added
 * to it, it will be committed at the end of the full tab switch operation.
 * This lets tab switches be atomic without the app needing to track
 * the interactions between different tabs.
 */
public class ABTabListener implements TabListener
{
    private Fragment mFragment;
    private FragmentManager mFragmentManager;

    public ABTabListener(Fragment fragment, FragmentManager fm)
    {
        mFragment = fragment;
        mFragmentManager = fm;
    }

    public void onTabSelected(Tab tab, FragmentTransaction unused)
    {
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
        }
        mFragmentManager.beginTransaction().replace(R.id.titles, mFragment, null).commit();
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