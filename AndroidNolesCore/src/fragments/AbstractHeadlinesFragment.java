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

package com.itnoles.shared.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.itnoles.shared.R;
import com.itnoles.shared.activities.BrowserDetailActivity;
import com.itnoles.shared.io.NewsListLoader;
import com.itnoles.shared.util.News;
import com.itnoles.shared.util.SharedPreferencesHelper;

import java.util.List;

public abstract class AbstractHeadlinesFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final int HEADLINE_LOADER = 0x0;

    // This is the Adapter being used to display the list's data.
    private NewsListAdapter mAdapter;
    private boolean mDualPane;
    private int mShownCheckPosition = -1;

    protected SharedPreferencesHelper mPrefsHelper;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Load Shared Preference Manager with helper
        final String packagePrefs = getActivity().getPackageName() + "_preferences";
        final SharedPreferences prefs = getActivity().getSharedPreferences(packagePrefs, Context.MODE_PRIVATE);
        mPrefsHelper = new SharedPreferencesHelper(prefs);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new NewsListAdapter(getActivity());
        setListAdapter(mAdapter);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.detail_frame);
        if (detailsFrame != null && detailsFrame.getVisibility() != View.VISIBLE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(HEADLINE_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Restart the Loaders for shared prefences changes when SP_KEY_NEWS_REFRESH is true
        if (mPrefsHelper.getNewsFresh()) {
            getLoaderManager().restartLoader(HEADLINE_LOADER, null, this);
            mPrefsHelper.setNewsRefreshToFalse();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final News news = (News) getListAdapter().getItem(position);
        final String urlString = news.getLink();
        if (mDualPane) {
            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                final BrowserDetailFragment df = BrowserDetailFragment.newInstance(urlString);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.detail_frame, df)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        } else {
            final Intent intent = new Intent(getActivity(), BrowserDetailActivity.class);
            intent.putExtra("url", urlString);
            startActivity(intent);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new NewsListLoader(getActivity(), getNewsURL());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // Set the new data in the adapter.
        mAdapter.setData(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Clear the data in the adapter.
        mAdapter.setData(null);
    }

    protected abstract String getNewsURL();

    static class NewsListAdapter extends ArrayAdapter<News> {
        private final LayoutInflater mLayoutInflater;

        public NewsListAdapter(Context context) {
            super(context, 0);
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<News> data) {
            clear();
            if (data != null) {
                for (News news : data) {
                    add(news);
                }
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid
            // unneccessary calls to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.headlines_item, null);

                // Creates a ViewHolder and store references to the three
                // children views we want to bind data to.
                holder = new ViewHolder();
                holder.mTitle = (TextView) convertView.findViewById(R.id.title);
                holder.mDate = (TextView) convertView.findViewById(R.id.date);
                holder.mDesc = (TextView) convertView.findViewById(R.id.description);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final News news = getItem(position);
            holder.mTitle.setText(news.getTitle());
            holder.mDate.setText(news.getPubDate());
            holder.mDesc.setText(news.getDesc());

            return convertView;
        }

        static class ViewHolder {
            TextView mTitle;
            TextView mDate;
            TextView mDesc;
        }
    }
}