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
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itnoles.shared.R;
import com.itnoles.shared.io.HeadlinesHandler;
import com.itnoles.shared.ui.phone.WebDetailsActivity;
import com.itnoles.shared.util.AQuery;
import com.itnoles.shared.util.NetworkUtils;
import com.itnoles.shared.util.News;
import com.itnoles.shared.util.ParserUtils;
import com.itnoles.shared.util.PlatformSpecificImplementationFactory;
import com.itnoles.shared.util.base.HttpTransport;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractHeadlinesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final String LOG_TAG = "HeadlinesFragment";

    protected SharedPreferences mSharedPrefs;

    // This is the Adapter being used to display the list's data.
    NewsListAdapter mAdapter;
    boolean mDualPane;
    int mShownCheckPosition = -1;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Give some text to display if there is no data. In a real
        // application this would come from a resource.
        setEmptyText("No applications");

        // Load Shared Preference Manager
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Set the actionbar's subtitle
        ((AbstractMainActivity) getActivity()).setActionBarSubtitle(getNewsTitle());

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new NewsListAdapter(getActivity());
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final AQuery aq = new AQuery(getActivity());
        mDualPane = aq.id(R.id.details).visible().isVisible();
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
            final Intent intent = new Intent();
            intent.setClass(getActivity(), WebDetailsActivity.class);
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

    protected abstract String getNewsTitle();
    protected abstract String getNewsURL();

    /**
     * Helper for determining if the configuration has changed in an interesting
     * way so we need to rebuild the app list.
     */
    static class InterestingConfigChanges {
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
    }

    /**
     * A custom Loader that loads all of the headlines.
     */
    static class NewsListLoader extends AsyncTaskLoader<List<News>> {
        final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();

        List<News> mNews;
        String mURL;

        public NewsListLoader(Context context, String url) {
            super(context);
            mURL = url;
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override
        public List<News> loadInBackground() {
            final HttpTransport transport = PlatformSpecificImplementationFactory.getTransport();
            try {
                final HttpTransport.LowLevelHttpResponse response = transport.buildResponse(mURL);
                final InputStream input = response.execute();
                try {
                    final XmlPullParser parser = ParserUtils.newPullParser(input);
                    final HeadlinesHandler handler = new HeadlinesHandler();
                    return handler.parse(parser);
                } catch(XmlPullParserException e) {
                    Log.w(LOG_TAG, "Malformed response", e);
                } finally {
                    if (input != null) {
                        input.close();
                    }
                    response.disconnect();
                }
            } catch(IOException e) {
                Log.w(LOG_TAG, "Problem reading remote response", e);
            } finally {
                transport.shutdown();
            }
            return null;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override
        public void deliverResult(List<News> news) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (news != null) {
                    onReleaseResources(news);
                }
            }
            final List<News> oldNews = news;
            mNews = news;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(news);
            }

            // At this point we can release the resources associated with
            // 'oldNews' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldNews != null) {
                onReleaseResources(oldNews);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (mNews != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mNews);
            }

            // Has something interesting in the configuration changed since we
            // last built the news list?
            final boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

            if (mNews == null || configChange && NetworkUtils.isNetworkConnected(getContext())) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override
        public void onCanceled(List<News> news) {
            super.onCanceled(news);

            // At this point we can release the resources associated with 'news'
            // if needed.
            onReleaseResources(news);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'news'
            // if needed.
            if (mNews != null) {
                onReleaseResources(mNews);
                mNews = null;
            }
        }

        /**
        * Helper function to take care of releasing resources associated
        * with an actively loaded data set.
        */
        protected void onReleaseResources(List<News> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }

    static class NewsListAdapter extends ArrayAdapter<News> {
        private LayoutInflater mLayoutInflater;

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
            final String text = news.getDesc();
            if (text.contains("<") && text.contains(">")) {
                holder.mDesc.setText(Html.fromHtml(text));
            } else {
                holder.mDesc.setText(text);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        TextView mTitle;
        TextView mDate;
        TextView mDesc;
    }
}