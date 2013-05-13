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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.nolesfootball.fragment;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itnoles.nolesfootball.activities.BrowserDetailActivity;
import com.itnoles.nolesfootball.R;
import com.itnoles.nolesfootball.SectionedListAdapter;
import com.itnoles.nolesfootball.io.NewsListLoader;
import com.itnoles.nolesfootball.io.model.News;

import java.util.List;

public class HeadlinesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final int HEADLINES_LOADER = 0x0;

    private String mTitle = "Top Athletics Stories";

    private boolean mDualPane;
    private int mShownCheckPosition = -1;
    private SectionedListAdapter mAdapter;

    public static HeadlinesFragment newInstance(String urlString) {
        HeadlinesFragment f = new HeadlinesFragment();

        // Supply url input as an argument.
        Bundle args = new Bundle();
        args.putString("url", urlString);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Don't do animation and progress indicatior, just show empty view
        setListShownNoAnimation(true);

        // The SectionedListAdapter is going to show news title
        mAdapter = new SectionedListAdapter(getActivity(), R.layout.list_section_header);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the detail view.
        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null && detailsFrame.getVisibility() != View.VISIBLE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(HEADLINES_LOADER, getArguments(), this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new NewsListLoader(getActivity(), args.getString("url"));
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        mAdapter.addSection(mTitle, new NewsListAdapter(getActivity(), data));
        setListAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Clear the data in the adapter.
        ((NewsListAdapter) mAdapter.getListAdapter(0)).clear();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        News news = (News) getListAdapter().getItem(position);
        String urlString = news.link;
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(position, true);

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                BrowserDetailFragment df = BrowserDetailFragment.newInstance(urlString);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, df)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        } else {
            Intent intent = new Intent(getActivity(), BrowserDetailActivity.class);
            intent.putExtra("url", urlString);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.headline_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = (String) item.getTitle();
        if (!mTitle.equals(title)) {
            mTitle = title;
        }

        switch (item.getItemId()) {
            case R.id.menu_athletics:
                reloadLoaderWithNewInformation("http://www.seminoles.com/headline-rss.xml");
                break;
            case R.id.menu_rivals:
                reloadLoaderWithNewInformation("http://floridastate.rivals.com/rss2feed.asp?SID=1061");
                break;
            case R.id.menu_scout:
                reloadLoaderWithNewInformation("http://rss.scout.com/rss.aspx?sid=16");
                break;
            case R.id.menu_tomahawk:
                reloadLoaderWithNewInformation("http://www.tomahawknation.com/rss/current");
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadLoaderWithNewInformation(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        getLoaderManager().restartLoader(HEADLINES_LOADER, bundle, this);
    }

    static class NewsListAdapter extends ArrayAdapter<News> {
        private final LayoutInflater mInflater;

        public NewsListAdapter(Context context, List<News> data) {
            super(context, 0, data);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * Populate new items in the list.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(android.R.id.text1);
                holder.date = (TextView) convertView.findViewById(android.R.id.text2);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            News news = getItem(position);
            holder.title.setText(news.title);
            holder.date.setText(news.getPubDate());

            return convertView;
        }

        static class ViewHolder {
            TextView title;
            TextView date;
        }
    }
}