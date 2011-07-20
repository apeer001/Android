/*
 * Copyright (C) 2011 Jonathan Steele
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

package com.itnoles.shared.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.Menu;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itnoles.shared.R;
import com.itnoles.shared.SportsApplication;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.io.RemoteExecutor;
import com.itnoles.shared.io.HeadlinesHandler;
import com.itnoles.shared.service.SyncService;
import com.itnoles.shared.ui.phone.SettingsActivity;
import com.itnoles.shared.ui.tablet.WebDetailsFragment;
import com.itnoles.shared.ui.tablet.SettingsMultiPaneActivity;
import com.itnoles.shared.util.FragmentUtils;
import com.itnoles.shared.util.News;
import com.itnoles.shared.util.UrlIntentListener;

import org.apache.http.client.HttpClient;

public class HeadlinesFragment extends ListFragment
{
    private static final String LOG_TAG = "HeadlinesFragment";
    private static HttpClient sHttpClient;

    private boolean mDualPane;
    private int mShownCheckPosition = -1;
    private String mPrefTitle;
    private SportsApplication mApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (!SportsConstants.SUPPORTS_HONEYCOMB) {
            return inflater.inflate(R.layout.headlines_view, null);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        mApplication = (SportsApplication) getActivity().getApplicationContext();

        // Create an empty adapter we will use to display the loaded data.
        setListAdapter(new NewsAdapter(getActivity()));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.details);
        // If users click in non-dual pane tabs,
        // it cause this one to be gone too.
        if (detailsFrame != null && detailsFrame.getVisibility() == View.GONE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        final String prefTitle = mApplication.getNewsTitle();
        if (!prefTitle.equals(mPrefTitle)) {
            if (getListAdapter() != null) {
                ((NewsAdapter) getListAdapter()).clear();
            }

            if (SportsConstants.SUPPORTS_HONEYCOMB) {
                getActivity().getSupportActionBar().setSubtitle(prefTitle);
            }
            else {
                final TextView subtitle = (TextView) getView().findViewById(R.id.list_header_title);
                subtitle.setText(prefTitle);
            }
            getNewContents();
            mPrefTitle = prefTitle;
        }
    }

    @Override
    public void onPause()
    {
        FragmentUtils.dispatchPause(mDualPane, this);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, android.view.MenuInflater inflater)
    {
        // Place an action bar item for settings.
        inflater.inflate(R.menu.newsmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
    {
        switch (item.getItemId()) {
        case R.id.refresh:
            getNewContents();
            return true;
        case R.id.settings:
            showSettings();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void getNewContents()
    {
        final ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Check to see if we are connected to a data network.
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            return;
        }
        new FeedLoadTask().execute(mApplication.getNewsURL());
    }

    private void showSettings()
    {
        if (SportsConstants.SUPPORTS_HONEYCOMB) {
            startActivity(new Intent(getActivity(), SettingsMultiPaneActivity.class));
        }
        else {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        if (mDualPane) {
            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                final String tag = v.getTag().toString();
                final WebDetailsFragment df = WebDetailsFragment.newInstance(tag);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.details, df)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        }
        else {
            l.setOnItemClickListener(new UrlIntentListener());
        }
    }

    private static synchronized HttpClient getHttpClient(Context context)
    {
        if (sHttpClient == null) {
            sHttpClient = SyncService.getHttpClient(context);
        }
        return sHttpClient;
    }

    private class FeedLoadTask extends AsyncTask<String, News, Void>
    {
        @Override
        protected Void doInBackground(String... params)
        {
            final String param = params[0];
            final HttpClient httpClient = getHttpClient(getActivity());
            final RemoteExecutor executor = new RemoteExecutor(httpClient, null);
            final HeadlinesHandler handler = new HeadlinesHandler();
            executor.executeWithSAXParser(param, handler);
            for (News value : handler.getFeeds()) {
                publishProgress(value);
            }
            return null;
	    }

	    @Override
	    protected void onProgressUpdate(News... values)
	    {
	        // If data is not null, add it to NewsAdapter.
	        if (values != null) {
                ((NewsAdapter) getListAdapter()).add(values[0]);
	        }
	    }
    }

    private static class NewsAdapter extends ArrayAdapter<News>
    {
        private LayoutInflater mLayoutInflater;

        public NewsAdapter(Context context)
        {
            super(context, 0);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
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
                convertView.setTag(R.id.headlines_viewholder, holder);
	        }
	        else {
	            holder = (ViewHolder) convertView.getTag(R.id.headlines_viewholder);
	        }

            final News news = getItem(position);
            holder.mTitle.setText(news.getTitle());
            holder.mDate.setText(news.getPubDate());
            final String text = news.getDesc();
            if (text.contains("<") && text.contains(">")) {
                holder.mDesc.setText(Html.fromHtml(text));
            }
            else {
                holder.mDesc.setText(text);
            }
            convertView.setTag(news.getLink());

            return convertView;
        }

        class ViewHolder
        {
            TextView mTitle;
            TextView mDate;
            TextView mDesc;
        }
    }
}