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

<<<<<<< HEAD
package com.itnoles.flavored.fragments;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
=======
package com.itnoles.flavored.fragment;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.os.StrictMode;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

<<<<<<< HEAD
import com.itnoles.flavored.*;
import com.itnoles.flavored.activities.BrowserDetailActivity;
import com.itnoles.flavored.model.News;
=======
import com.android.volley.Response.Listener;
//import com.android.volley.toolbox.NetworkImageView;
import com.itnoles.flavored.activities.BrowserDetailActivity;
import com.itnoles.flavored.R;
import com.itnoles.flavored.util.AbstractXMLRequest;
import com.itnoles.flavored.util.VolleyHelper;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
<<<<<<< HEAD
import java.io.InputStreamReader;
=======
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
import java.util.ArrayList;
import java.util.List;

import static com.itnoles.flavored.BuildConfig.NEWS_URL;

<<<<<<< HEAD
public class HeadlinesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final String LOG_TAG = "HeadlinesFragment";

    private boolean mDualPane;
    private int mShownCheckPosition = -1;
=======
public class HeadlinesFragment extends ListFragment {
    private boolean mDualPane;
    private int mShownCheckPosition = -1;
    private NewsListAdapter mAdapter;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Create an empty adapter we will use to display the loaded data.
<<<<<<< HEAD
        NewsListAdapter adapter = new NewsListAdapter(getActivity());
        setListAdapter(adapter);
=======
        mAdapter = new NewsListAdapter(getActivity());
        setListAdapter(mAdapter);
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511

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

<<<<<<< HEAD
        // Prepare the loader. Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new NewsLoader(getActivity());
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
=======
        loadData();
    }

    private void loadData() {
        // Start default url load with Volley.
        NewsRequests nr = new NewsRequests(new Listener<List<News>>() {
            @Override
            public void onResponse(List<News> response) {
                mAdapter.addAll(response);
            }
        });
        VolleyHelper.getResultQueue().add(nr);
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
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

<<<<<<< HEAD
    static class NewsLoader extends AbstractContentListLoader<News> {
        NewsLoader(Context context) {
            super(context);
        }

        /**
         * This is where the bulk of our work is done. This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override
        public List<News> loadInBackground() {
            mResults = new ArrayList<News>();
            InputStreamReader reader = null;
            try {
                reader = Utils.openUrlConnection(NEWS_URL);
                XmlPullParser parser = XMLUtils.parseXML(reader);
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
                        mResults.add(currentNews);
                    }
                }
            } catch (XmlPullParserException xppe) {
                Log.w(LOG_TAG, "Problem on parsing xml file", xppe);
            } catch (IOException ioe) {
                Log.w(LOG_TAG, "Problem on xml file", ioe);
            } finally {
                Utils.ignoreQuietly(reader);
            }
            return mResults;
=======
    static class NewsRequests extends AbstractXMLRequest<List<News>> {
        NewsRequests(Listener<List<News>> listener) {
            super(NEWS_URL, listener);
        }

        @Override
        public List<News> onPostNetworkResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
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
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
        }
    }

    private class NewsListAdapter extends ArrayAdapter<News> {
        public NewsListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
<<<<<<< HEAD
            // A ViewHolder keeps references to children views to avoid unnecessary calls
            // to findViewById() on each row.
            ViewHolder holder = ViewHolder.get(convertView, parent);
=======
            View view;

            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            if (convertView == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.headlines_item, parent, false);

                holder = new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.date = (TextView) view.findViewById(R.id.date);
                holder.desc = (TextView) view.findViewById(R.id.desc);

                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511

            News news = getItem(position);
            holder.title.setText(news.title);
            holder.date.setText(news.pubDate);
            holder.desc.setText(news.desc);

<<<<<<< HEAD
            return holder.root;
        }
    }

    static class ViewHolder {
        public final View root;
        public final TextView title;
        public final TextView date;
        public final TextView desc;

        private ViewHolder(ViewGroup parent) {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.headlines_item, parent, false);
            root.setTag(this);

            title = (TextView) root.findViewById(R.id.title);
            date = (TextView) root.findViewById(R.id.date);
            desc = (TextView) root.findViewById(R.id.desc);
        }

        public static ViewHolder get(View convertView, ViewGroup parent) {
            if (convertView == null) {
                return new ViewHolder(parent);
            }
            return (ViewHolder) convertView.getTag();
        }
=======
            return view;
        }
    }

    static class News {
        public String title;
        public String link;
        public String desc;
        public String pubDate;
        //public String imageURL;

        void setValue(String key, String value) {
            if ("title".equals(key)) {
                title = value;
            } else if ("pubDate".equals(key)) {
                pubDate = value;
            } else if ("link".equals(key)) {
                link = value;
            } else if ("description".equals(key)) {
                desc = value.replaceAll("\\<.*>","");
            } /*else if ("enclosure".equals(key)) {
                imageURL = value;
            }*/
        }
    }

    static class ViewHolder {
        TextView title;
        TextView date;
        TextView desc;
        //NetworkImageView thumbnail;
>>>>>>> 2286f96e013c12e773d943ea08e6cf4abeeb1511
    }
}