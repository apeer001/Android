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

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.inoles.nolesfootball.model.Rosters;
import com.inoles.nolesfootball.parser.RostersXMLParser;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;

public class RostersActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new RostersFragment())
                    .commit();
        }
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity.
     */
    @Override
    int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_ROSTERS;
    }

    public static class RostersFragment extends ListFragment
            implements SearchView.OnQueryTextListener {
        private static final String LOG_TAG = RostersFragment.class.getName();

        RostersListAdapter mAdapter;
        Subscription subscription;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // We have a menu item to show in toolbar.
            setHasOptionsMenu(true);

            // Create an empty adapter we will use to display the loaded data.
            mAdapter = new RostersListAdapter(getActivity());

            RostersXMLParser parser = new RostersXMLParser();
            subscription = parser.pullDataFromNetwork()
                    .compose(RxUtils.<Rosters>applyFragmentSchedulers(this))
                    .subscribe(new Subscriber<List<Rosters>>() {
                        @Override
                        public void onCompleted() {
                            setListAdapter(mAdapter);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(LOG_TAG, Log.getStackTraceString(e));
                        }

                        @Override
                        public void onNext(List<Rosters> rostersList) {
                            mAdapter.add(rostersList);
                        }
                    });
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.search, menu);
            final SearchView searchView = (SearchView) MenuItemCompat
                    .getActionView(menu.findItem(R.id.menu_search));
            searchView.setOnQueryTextListener(this);
        }

        @Override
        public boolean onQueryTextChange(String s) {
            String newText = !TextUtils.isEmpty(s) ? s : null;
            if (newText != null) {
                mAdapter.getFilter().filter(newText);
            }
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (subscription != null) {
                subscription.unsubscribe();
            }
        }
    }
}
