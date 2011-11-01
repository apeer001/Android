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
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ModernAsyncTask;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.itnoles.shared.R;
import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.io.HeadlinesHandler;
import com.itnoles.shared.ui.phone.WebDetailsActivity;
import com.itnoles.shared.util.News;
import com.itnoles.shared.util.ParserUtils;
import com.itnoles.shared.util.PlatformSpecificImplementationFactory;
import com.itnoles.shared.util.base.HttpTransport;
import com.itnoles.shared.util.base.ISubTitle;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractHeadlinesFragment extends ListFragment
{
    public static final String LOG_TAG = "HeadlinesFragment";
    private boolean mDualPane;
    private int mShownCheckPosition = -1;

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

        if (getListAdapter() != null) {
            ((NewsAdapter) getListAdapter()).clear();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, android.view.MenuInflater inflater)
    {
        // Place an action bar item for settings.
        inflater.inflate(R.menu.newsmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        final int getItemId = item.getItemId();
        if (getItemId == R.id.settings) {
            showSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
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
        }
        else {
            final Intent intent = new Intent();
            intent.setClass(getActivity(), WebDetailsActivity.class);
            intent.putExtra("url", urlString);
            startActivity(intent);
        }
    }

    protected abstract void showSettings();

    protected void setSubTitle(String subtitle)
    {
        final ISubTitle getSubtitle = PlatformSpecificImplementationFactory.getSubTitle();
        getSubtitle.displaySubTitle(this, subtitle);
    }

    protected ModernAsyncTask<String, News, Void> getLoadNewsTask()
    {
        return new ModernAsyncTask<String, News, Void>() {
            @Override
            protected Void doInBackground(String... params)
            {
                final String param = params[0];
                final HttpTransport transport = PlatformSpecificImplementationFactory.getTransport(getActivity());
                if (transport == null) {
                    return null;
                }
                try {
                    final HttpTransport.LowLevelHttpResponse response = transport.buildResponse(param);
                    final InputStream input = response.execute();
                    try {
                        final XmlPullParser parser = ParserUtils.newPullParser(input);
                        final HeadlinesHandler handler = new HeadlinesHandler();
                        final List<News> news = handler.parse(parser);
                        if (news != null) {
                            for (News value : news) {
                                publishProgress(value);
                            }
                        }
                    }
                    catch(XmlPullParserException e) {
                        Log.w(LOG_TAG, "Malformed response", e);
                    }
                    finally {
                        if (input != null) {
                            input.close();
                        }
                        response.disconnect();
                    }
                }
                catch(IOException e) {
                    Log.w(LOG_TAG, "Problem reading remote response", e);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(News... values)
            {
                ((NewsAdapter) getListAdapter()).add(values[0]);
            }
        };
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

                final AQuery aq = new AQuery(convertView);

                // Creates a ViewHolder and store references to the three
                // children views we want to bind data to.
                holder = new ViewHolder();
                holder.mTitle = aq.id(R.id.title).getTextView();
                holder.mDate = aq.id(R.id.date).getTextView();
                holder.mDesc = aq.id(R.id.description).getTextView();
                convertView.setTag(R.id.headlines_viewholder, holder);
	        }
	        else {
                final AQuery aq = new AQuery(convertView);
	            holder = (ViewHolder) aq.getTag(R.id.headlines_viewholder);
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

            return convertView;
        }
    }

    static class ViewHolder
    {
        TextView mTitle;
        TextView mDate;
        TextView mDesc;
    }
}