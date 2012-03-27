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

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itnoles.shared.BuildConfig;
import com.itnoles.shared.R;

//import java.io.File;
import java.lang.reflect.Method;

public abstract class AbstractTabbedActivity extends SherlockFragmentActivity {
    private static final String LOG_TAG = "TabbedActivity";

    protected ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            try {
                final Class<?> strictMode = Class.forName("android.os.StrictMode");
                final Method enableDefaults = strictMode.getMethod("enableDefaults");
                enableDefaults.invoke(null);
            } catch (Exception e) {
                //The version of Android we're on doesn't have android.os.StrictMode
                //so ignore this exception
                Log.d(LOG_TAG, "Strict mode not available");
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layer);

        /*new Thread(new Runnable() {
            public void run() {
                enableHttpResponseCache();
            }
        }).start();*/

        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_activity, menu);
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

    //XXX: It seems there is a problem for this in httpurlconnection on 2nd times.
    /*private void enableHttpResponseCache() {
        try {
            final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            final File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                 .getMethod("install", File.class, long.class)
                 .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d(LOG_TAG, "HTTP response cache is unavailable.");
        }
    }*/

    /**
     * A TabListener receives event callbacks from the action bar as tabs
     * are deselected, selected, and reselected. A FragmentTransaction
     * is provided to each of these callbacks; if any operations are added
     * to it, it will be committed at the end of the full tab switch operation.
     * This lets tab switches be atomic without the app needing to track
     * the interactions between different tabs.
     */
    public class TabListener implements ActionBar.TabListener {
        private final SherlockListFragment mFragment;

        public TabListener(SherlockListFragment fragment) {
            this.mFragment = fragment;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.content_frame, mFragment, null);
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            // do nothing
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // do nothing
        }
    }
}