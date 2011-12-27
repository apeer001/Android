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

package com.itnoles.shared.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
//import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itnoles.shared.R;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.io.AsyncListLoader;
import com.itnoles.shared.io.HeadlinesHandler;
import com.itnoles.shared.ui.phone.WebDetailsActivity;
import com.itnoles.shared.util.AQuery;
import com.itnoles.shared.util.NetworkUtils;
import com.itnoles.shared.util.News;
import com.itnoles.shared.util.PlatformSpecificImplementationFactory;
import com.itnoles.shared.util.UIUtils;
import com.itnoles.shared.util.XMLPullParserUtils;
import com.itnoles.shared.util.base.HttpTransport;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public abstract class AbstractHeadlinesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
    protected SharedPreferences mSharedPrefs;

    // This is the Adapter being used to display the list's data.
    NewsListAdapter mAdapter;
    boolean mDualPane;
    int mShownCheckPosition = -1;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Give some text to display if there is no data.
        setEmptyText(getString(R.string.empty_headlines));

        // Load Shared Preference Manager
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new NewsListAdapter(getActivity());
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        mDualPane = UIUtils.checkDualPaneForTablet(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Restart the Loaders for shared prefences changes when SP_KEY_NEWS_REFRESH is true
        if (mSharedPrefs.getBoolean(SportsConstants.SP_KEY_NEWS_REFRESH, false)) {
            getLoaderManager().restartLoader(0, null, this);
            mSharedPrefs.edit().putBoolean(SportsConstants.SP_KEY_NEWS_REFRESH, false).commit();
        }

        // Set the actionbar's subtitle
        final String title = mSharedPrefs.getString(SportsConstants.SP_KEY_NEWS_TITLE, "Latest Football news");
        ((AbstractMainActivity) getActivity()).setActionBarSubtitle(title);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final News news = (News) getListAdapter().getItem(position);
        final String urlString = news.getLink();

        if (mDualPane) {
            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                final WebDetailsFragment df = WebDetailsFragment.newInstance(urlString);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.details, df)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        } else {
            final Intent intent = new Intent(getActivity(), WebDetailsActivity.class);
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

    /**
     * Helper for determining if the configuration has changed in an interesting
     * way so we need to rebuild the news list.
     */
    /*static class InterestingConfigChanges {
        final Configuration mLastConfiguration = new Configuration();
        int mLastDensity;

        boolean applyNewConfig(Resources res) {
            final int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
            final boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
            if (densityChanged || (configChanges&(ActivityInfo.CONFIG_LOCALE
                |ActivityInfo.CONFIG_UI_MODE|ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
                mLastDensity = res.getDisplayMetrics().densityDpi;
                return true;
            }
            return false;
        }
    }*/

    /**
     * A custom Loader that loads all of the headlines.
     */
    static class NewsListLoader extends AsyncListLoader<List<News>> {
        String mURL;

        public NewsListLoader(Context context, String url) {
            super(context);
            mURL = url;
        }

        /**
         * This is where the bulk of our work is done. This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override
        public List<News> loadInBackground() {
            final NetworkUtils network = new NetworkUtils(getContext());
            if (!network.isNetworkConnected()) {
                return null;
            }
            final HeadlinesHandler handler = new HeadlinesHandler();
            final HttpTransport transport = PlatformSpecificImplementationFactory.getTransport();
            try {
                XMLPullParserUtils.execute(transport, mURL, new XMLPullParserUtils.XMLPullParserManager() {
                    public void onPostExecute(XmlPullParser parser) throws XmlPullParserException, IOException {
                        handler.parse(parser);
                    }
                });
            } finally {
                transport.shutdown();
            }
            return handler.getNews();
        }
    }

    static class NewsListAdapter extends ArrayAdapter<News> {
        private final LayoutInflater mLayoutInflater;

        public NewsListAdapter(Context context) {
            super(context, 0);
            mLayoutInflater = LayoutInflater.from(context);
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

                final AQuery aq = new AQuery(convertView);

                // Creates a ViewHolder and store references to the three
                // children views we want to bind data to.
                holder = new ViewHolder();
                holder.mTitle = aq.id(R.id.title).getTextView();
                holder.mDate = aq.id(R.id.date).getTextView();
                holder.mDesc = aq.id(R.id.description).getTextView();
                convertView.setTag(R.id.headlines_viewholder, holder);
	        } else {
                final AQuery aq = new AQuery(convertView);
	            holder = (ViewHolder) aq.getTag(R.id.headlines_viewholder);
	        }

            final News news = getItem(position);
            holder.mTitle.setText(news.getTitle());
            holder.mDate.setText(news.getPubDate());
            UIUtils.setTextMaybeHtml(holder.mDesc, news.getDesc());

            return convertView;
        }
    }

    static class ViewHolder {
        TextView mTitle;
        TextView mDate;
        TextView mDesc;
    }
}