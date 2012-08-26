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

package com.itnoles.shared.fragment;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class HeadlinesFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
    protected static final int HEADLINES_LOADER = 0x0;

    static final int INTERNAL_PROGRESS_CONTAINER_ID = 0x00ff0002;
    static final int INTERNAL_EMPTY_ID = 0x00ff0001;
    static final int INTERNAL_LIST_CONTAINER_ID = 0x00ff0003;

    protected boolean mDualPane;
    protected int mShownCheckPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.headlines_fragment, container, false);
        root.findViewById(R.id.progressContainer).setId(INTERNAL_PROGRESS_CONTAINER_ID);
        root.findViewById(R.id.internalEmpty).setId(INTERNAL_EMPTY_ID);
        root.findViewById(R.id.listContainer).setId(INTERNAL_LIST_CONTAINER_ID);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Give some text to display if there is no data.
        setEmptyText(getString(R.string.noncontent));

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        setListAdapter(new NewsListAdapter(getActivity()));

        // Show the header title
        setHeaderTitle(getArguments().getString("title"));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(HEADLINES_LOADER, getArguments(), this);
    }

    private void setHeaderTitle(String header) {
        final TextView headerView = (TextView) getView().findViewById(R.id.section_title);
        if (headerView != null) {
            headerView.setText(header);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // Start out with a progress indicator.
        setListShown(false);

        // This is called when a new Loader needs to be created.
        return new NewsListLoader(getActivity(), args.getString("url"));
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // Set the new data in the adapter.
        ((NewsListAdapter) getListAdapter()).setData(data);

        // The list should now be shown.
        //if (isResumed()) {
            setListShown(true);
        //} else {
            //setListShownNoAnimation(true);
        //}
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Clear the data in the adapter.
        ((NewsListAdapter) getListAdapter()).setData(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final News news = (News) getListAdapter().getItem(position);
        final String urlString = news.getLink();
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(position, true);

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                final BrowserDetailFragment df = BrowserDetailFragment.newInstance(urlString);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, df)
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

    protected void reloadLoaderWithNewInformation(String title, String url) {
        final Bundle bundle = new Bundle();
        bundle.putString("url", url);
        getLoaderManager().restartLoader(HEADLINES_LOADER, bundle, this);
        setHeaderTitle(title);
    }

    static class NewsListAdapter extends ArrayAdapter<News> {
        public NewsListAdapter(Context context) {
            super(context, 0);
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
            final ViewHolder holder = ViewHolder.get(convertView, parent);
            final News news = getItem(position);
            holder.mTitle.setText(news.getTitle());
            holder.mDate.setText(news.getPubDate());
            holder.mDesc.setText(news.getDesc());

            return holder.mRoot;
        }
    }

    static class ViewHolder {
        public final View mRoot;
        public final TextView mTitle;
        public final TextView mDate;
        public final TextView mDesc;

        private ViewHolder(ViewGroup parent) {
            mRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.headlines_item, null);
            mRoot.setTag(this);

            mTitle = (TextView) mRoot.findViewById(R.id.title);
            mDate = (TextView) mRoot.findViewById(R.id.date);
            mDesc = (TextView) mRoot.findViewById(R.id.description);
        }

        public static ViewHolder get(View convertView, ViewGroup parent) {
            if (convertView == null) {
                return new ViewHolder(parent);
            }
            return (ViewHolder) convertView.getTag();
        }
    }
}