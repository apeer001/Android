/*
 * Copyright 2014 Google Inc. All rights reserved.
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

package com.inoles.nolesfootball;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;

import com.inoles.nolesfootball.model.DrawerPrimaryAction;

import java.util.ArrayList;
import java.util.List;

/**
 * A base activity that handles common functionality in the app. This includes the
 * navigation drawer
 */
abstract class BaseActivity extends ActionBarActivity
        implements SideDrawerAdapter.OnItemClickListener {

    // Navigation drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    // symbols for nav drawer items (indices must correspond to array below).
    static final int NAVDRAWER_ITEM_HEADLINES = 0;
    static final int NAVDRAWER_ITEM_SCHEDULES = 1;
    static final int NAVDRAWER_ITEM_ROSTERS = 2;
    private static final int NAVDRAWER_ITEM_STANDINGS = 3;

    // Primary toolbar and drawer toggle
    Toolbar mActionBarToolbar;

    /**
     * Returns the navigation drawer item that corresponds to this Activity.
     */
    int getSelfNavDrawerItem() {
        return -1;
    }

    /**
     * Returns the default layout resource or subclass override it
     */
    int getLayoutResource() { return R.layout.base_activity; }

    private void createPrimaryActions(List<DrawerPrimaryAction> actions) {
        actions.add(new DrawerPrimaryAction("Headlines", 0));
        actions.add(new DrawerPrimaryAction("Schedules", 0));
        actions.add(new DrawerPrimaryAction("Rosters", 0));
        //actions.add(new DrawerPrimaryAction("Standings", 0));
    }

    private void createSecondaryActions(List<String> actions) {
        actions.add("Twitter");
        actions.add("Tickets");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	if (BuildConfig.DEBUG) {
    		StrictMode.enableDefaults();
    	}
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);

        setupNavDrawer();
    }

    /**
     * Sets up the navigation drawer as appropriate.
     */
    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        // The drawer title must be set in order to announce state changes when
        // accessibility is turned on. This is typically a simple description,
        // e.g. "Navigation".
        mDrawerLayout.setDrawerTitle(Gravity.START, getString(R.string.drawer_title));

        List<DrawerPrimaryAction> primary = new ArrayList<>();
        createPrimaryActions(primary);

        List<String> secondary = new ArrayList<>();
        createSecondaryActions(secondary);

        RecyclerView drawerList = (RecyclerView) findViewById(R.id.sports_drawer_list);

        // improve performance by indicating the list if fixed size.
        drawerList.setHasFixedSize(true);
        drawerList.setLayoutManager(new LinearLayoutManager(this));

        // set up the drawer's list view with items and click listener
        drawerList.setAdapter(new SideDrawerAdapter(getSelfNavDrawerItem(), primary, secondary,
                this));

        // ActionBarDrawerToggle provides convenient helpers for tying together the
        // prescribed interactions between a top-level sliding drawer and the action bar.
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mActionBarToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
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
            case NAVDRAWER_ITEM_STANDINGS:
                /*intent = new Intent(this, StandingActivity.class);
                startActivity(intent);
                finish();*/
                break;
            case 4:
                gotoURL("https://twitter.com/Seminoles_com");
                break;
            case 5:
                gotoURL("http://www.seminoles.com/ViewArticle.dbml?ATCLID=209576083&DB_OEM_ID=32900&DB_OEM_ID=32900");
                break;
        }
    }

    private void gotoURL(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(int position) {
        if (position == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        goToNavDrawerItem(position);

        mDrawerLayout.closeDrawer(Gravity.START);
    }
}
