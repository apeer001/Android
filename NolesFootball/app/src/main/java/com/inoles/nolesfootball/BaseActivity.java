/*
 * Copyright (C) 2015 Jonathan Steele
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

package com.inoles.nolesfootball;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * A base activity that handles common functionality in the app. This includes the
 * navigation drawer
 */
abstract class BaseActivity extends ActionBarActivity
{
    // symbols for nav drawer_item items
    static final int NAVDRAWER_ITEM_HEADLINES = 0;
    static final int NAVDRAWER_ITEM_SCHEDULES = 1;
    static final int NAVDRAWER_ITEM_ROSTERS = 2;
    //static final int NAVDRAWER_ITEM_STANDINGS = 3;

    // Navigation Drawer Toggle
    private ActionBarDrawerToggle mDrawerToggle;

    // Primary toolbar
    private Toolbar mActionBarToolbar;

    /**
     * Returns the navigation drawer item that corresponds to this Activity.
     */
    int getSelfNavDrawerItem() {
        return -1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	if (BuildConfig.DEBUG) {
    		StrictMode.enableDefaults();
    	}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);

        setupNavDrawer();
    }

    /**
     * Sets up the navigation drawer as appropriate.
     */
    private void setupNavDrawer() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            return;
        }

        // The drawer title must be set in order to announce state changes when
        // accessibility is turned on.
        drawerLayout.setDrawerTitle(Gravity.START, getString(R.string.drawer_title));

        String[] titles = getResources().getStringArray(R.array.navdrawer_titles);

        final int selectedItem = getSelfNavDrawerItem();

        ListView drawerList = (ListView) findViewById(R.id.sports_drawer_list);
        // set up the drawer's list view with items and click listener
        drawerList.setAdapter(new SideDrawerAdapter(this, selectedItem, titles));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull AdapterView<?> adapterView, @NonNull View view, int position, long l) {
                if (position == selectedItem) {
                    drawerLayout.closeDrawer(Gravity.START);
                    return;
                }

                goToNavDrawerItem(position);

                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        // ActionBarDrawerToggle provides convenient helpers for tying together the
        // prescribed interactions between a top-level sliding drawer and the toolbar.
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                mActionBarToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) {
            case NAVDRAWER_ITEM_HEADLINES:
                intent = new Intent(this, HeadlinesActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_SCHEDULES:
                intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_ROSTERS:
                intent = new Intent(this, RostersActivity.class);
                startActivity(intent);
                finish();
                break;
            /*case NAVDRAWER_ITEM_STANDINGS:
                intent = new Intent(this, StandingActivity.class);
                startActivity(intent);
                finish();
                break;*/
            case 3:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Seminoles_com"));
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.seminoles.com/ViewArticle.dbml?ATCLID=209576083&DB_OEM_ID=32900&DB_OEM_ID=32900"));
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
    }
}
