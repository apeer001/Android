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

package com.itnoles.flavored;

import android.app.*; //ActionBar, Activity, Fragment, FragmentManager, FragmentTransaction
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;

import com.itnoles.flavored.fragment.HeadlinesFragment;
import com.itnoles.flavored.fragment.RostersFragment;
//import com.itnoles.flavored.fragment.ScheduleFragment;

public class MainActivity extends Activity implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        VolleyHelper.init(this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new HomePagerAdapter(getFragmentManager()));
        mViewPager.setOnPageChangeListener(this);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText("News").setTabListener(this));
        //actionBar.addTab(actionBar.newTab().setText("Schedule").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Roster").setTabListener(this));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyHelper.getResultQueue().cancelAll(this);
    }

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
                    //return new ScheduleFragment();
                //case 2:
                    return new RostersFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}