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

package com.itnoles.flavored.fragments;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itnoles.flavored.activities.BrowserDetailActivity;
import com.itnoles.flavored.R;
import com.itnoles.flavored.model.News;
import com.itnoles.flavored.ViewHolder;
import com.itnoles.flavored.XMLContentLoader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeadlinesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
    private boolean mDualPane;
    private int mShownCheckPosition = -1;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Create an empty adapter we will use to display the loaded data.
        NewsListAdapter adapter = new NewsListAdapter();
        setListAdapter(adapter);

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
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new XMLContentLoader<News>(getActivity(), args.getString("url"), new NewsLoader());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        // Set the new data in the adapter.
        ((NewsListAdapter) getListAdapter()).addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Clear the data in the adapter.
        ((NewsListAdapter) getListAdapter()).clear();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        News news = (News) getListAdapter().getItem(position);
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(position, true);

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                BrowserDetailFragment df = BrowserDetailFragment.newInstance(news.link);

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
            intent.putExtra("url", news.link);
            startActivity(intent);
        }
    }

    private static class NewsLoader implements XMLContentLoader.ResponseListener<News> {
        @Override
        public List<News> onPostExecute(XmlPullParser parser) throws IOException, XmlPullParserException {
            List<News> results = new ArrayList<News>();
            // The News that is currently being parsed
            News currentNews = null;
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if ("item".equals(name)) {
                        currentNews = new News();
                    } else if (currentNews != null) {
                        //if ("enclosure".equals(name)) {
                        //currentNews.setValue("enclosure", parser.getAttributeValue(null, "url"));
                        //} else {
                        currentNews.setValue(name, parser.nextText());
                        //}
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG && "item".equals(parser.getName())) {
                    results.add(currentNews);
                }
            }
            return results;
        }
    }

    private class NewsListAdapter extends ArrayAdapter<News> {
        public NewsListAdapter() {
            super(getActivity(), 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.headlines_item, parent, false);
            }

            News news = getItem(position);

            TextView title = ViewHolder.get(convertView, R.id.title);
            title.setText(news.title);

            TextView date = ViewHolder.get(convertView, R.id.date);
            date.setText(news.pubDate);

            TextView desc = ViewHolder.get(convertView, R.id.desc);
            desc.setText(news.desc);

            return convertView;
        }
    }
}