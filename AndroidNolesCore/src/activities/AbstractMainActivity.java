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

package com.itnoles.shared.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.itnoles.shared.BuildConfig;
import com.itnoles.shared.R;
import com.itnoles.shared.util.LogUtils;

import java.io.File;
import java.util.ArrayList;

public abstract class AbstractMainActivity extends SherlockFragmentActivity {
    private static final String LOG_TAG = LogUtils.makeLogTag(AbstractMainActivity.class);
    private static final boolean SUPPORTS_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG && SUPPORTS_GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final AsyncTask<Void, Void, Void> enableCache = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... urls) {
                enableHttpResponseCache();
                return null;
            }
        };
        enableCache.execute();
    }

    private void enableHttpResponseCache() {
        final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
        final File httpCacheDir = new File(getCacheDir(), "http");
        try {
            Class.forName("android.net.http.HttpResponseCache")
                 .getMethod("install", File.class, long.class)
                 .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            try {
                com.integralblue.httpresponsecache.HttpResponseCache.install(httpCacheDir, httpCacheSize);
            } catch (Exception e) {
                LogUtils.LOGE(LOG_TAG, "Failed to set up com.integralblue.httpresponsecache.HttpResponseCache", e);
            }
        }
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated ActionBar. It relies on a
     * trick. This is not sufficient for switching between pages. It listens to
     * changes in tabs, and takes care of switch to the correct paged in the
     * ViewPager whenever the selected tab changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> mClss;
            private final Bundle mArgs;

            TabInfo(Class<?> clss, Bundle args) {
                mClss = clss;
                mArgs = args;
            }
        }

        public TabsAdapter(SherlockFragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            this.mContext = activity;
            this.mActionBar = activity.getSupportActionBar();
            this.mViewPager = (ViewPager) activity.findViewById(R.id.pager);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(Tab tab, Class<?> clss, Bundle args) {
            final TabInfo info = new TabInfo(clss, args);
            mTabs.add(info);
            tab.setTabListener(this);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            final TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.mClss.getName(), info.mArgs);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }
}