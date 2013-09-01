/*
 * Copyright (C) 2013 Jonathan Steele
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.flavored.activities;

import android.app.*; //ActionBar, Activity, Fragment, FragmentManager, FragmentTransaction
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;

import com.itnoles.flavored.BuildConfig;
<<<<<<< HEAD
import com.itnoles.flavored.fragments.HeadlinesFragment;
import com.itnoles.flavored.fragments.RostersFragment;
import com.itnoles.flavored.fragments.ScheduleFragment;
import com.itnoles.flavored.R;
=======
import com.itnoles.flavored.fragment.HeadlinesFragment;
import com.itnoles.flavored.fragment.RostersFragment;
//import com.itnoles.flavored.fragment.ScheduleFragment;
import com.itnoles.flavored.R;
import com.itnoles.flavored.util.VolleyHelper;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511

public class MainActivity extends Activity implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
<<<<<<< HEAD
        // Enable Strict Mode under debug mode
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

=======
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        VolleyHelper.init(this);

>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new HomePagerAdapter(getFragmentManager()));
        mViewPager.setOnPageChangeListener(this);

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.addTab(bar.newTab().setText("News").setTabListener(this));
<<<<<<< HEAD
        bar.addTab(bar.newTab().setText("Schedule").setTabListener(this));
=======
        //bar.addTab(bar.newTab().setText("Schedule").setTabListener(this));
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
        bar.addTab(bar.newTab().setText("Roster").setTabListener(this));
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

<<<<<<< HEAD
=======
    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyHelper.getResultQueue().stop();
    }

>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
    private static class HomePagerAdapter extends FragmentPagerAdapter {
        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HeadlinesFragment();
                case 1:
<<<<<<< HEAD
                    return new ScheduleFragment();
               case 2:
=======
                    //return new ScheduleFragment();
                //case 2:
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
                    return new RostersFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
<<<<<<< HEAD
            return 3;
=======
            return 2;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
        }
    }
}