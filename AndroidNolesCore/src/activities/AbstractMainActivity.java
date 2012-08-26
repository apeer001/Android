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

import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.itnoles.shared.BuildConfig;
import com.itnoles.shared.R;

import java.io.*;

import static com.itnoles.shared.util.LogUtils.makeLogTag;
import static com.itnoles.shared.util.LogUtils.LOGE;

public abstract class AbstractMainActivity extends SherlockFragmentActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
    private static final String LOG_TAG = makeLogTag(AbstractMainActivity.class);
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final boolean SUPPORTS_GINGERBREAD = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
        if (BuildConfig.DEBUG && SUPPORTS_GINGERBREAD) {
            StrictMode.enableDefaults();
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

        mViewPager = (ViewPager) findViewById(R.id.pager);
    }

    protected void addViewPagerWithTab(FragmentPagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(this);

        final ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.addTab(bar.newTab().setText("News").setTabListener(this));
        bar.addTab(bar.newTab().setText("Team").setTabListener(this));
        bar.addTab(bar.newTab().setText("Link").setTabListener(this));
    }

    private void enableHttpResponseCache() {
        final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
        final File httpCacheDir = new File(getCacheDir(), "http");
        try {
            final boolean SUPPORTS_JELLYBEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
            if (SUPPORTS_JELLYBEAN) {
                HttpResponseCache.install(httpCacheDir, httpCacheSize);
            } else {
                com.integralblue.httpresponsecache.HttpResponseCache.install(httpCacheDir, httpCacheSize);
            }
        } catch (IOException ioe) {
            LOGE(LOG_TAG, "Failed to set up HttpResponseCache", ioe);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onPageScrolled(int i, float v, int i1) {}

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}