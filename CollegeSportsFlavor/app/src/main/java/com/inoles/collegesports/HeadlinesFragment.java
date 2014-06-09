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

package com.inoles.collegesports;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class HeadlinesFragment extends ListFragment {
    static class News {
        String Title;
        String Link;
        String PubDate;

    }

    private static final String LOG_TAG = "HeadlinesFragment";

    private List<News> mResult = new ArrayList<>();
    private NewsListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new NewsListAdapter(getActivity());

        StringReaderRequest xr = new StringReaderRequest(BuildConfig.NEWS_URL, new Response.Listener<StringReader>() {
            @Override
            public void onResponse(StringReader response) {
                load(response);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LOG_TAG, error.getMessage());
            }
        });
        xr.setTag(this);

       MainActivity.sQueue.add(xr);
    }

    void load(StringReader reader) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(reader);
            // The News that is currently being parsed
            News currentNews = null;
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("item".equals(name)) {
                        currentNews = new News();
                    } else if (currentNews != null) {
                        switch(name) {
                            case "pubDate":
                                currentNews.PubDate = parser.nextText();
                                break;
                            case "title":
                                currentNews.Title = parser.nextText();
                                break;
                            case "link":
                                currentNews.Link = parser.nextText();
                                break;
                            default:
                        }
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG && "item".equals(name)) {
                    mResult.add(currentNews);
                }
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            reader.close();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        News news = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), BrowserDetailActivity.class);
        intent.putExtra("url", news.Link);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mResult.clear();
        mResult = null;
    }

    class NewsListAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;

        public NewsListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mResult.size();
        }

        @Override
        public News getItem(int position) {
            return mResult.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Nullable
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            // A ViewHolder keeps references to children views to avoid unnecessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to re-inflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder(convertView);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the NetworkImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            News item = getItem(position);
            holder.title.setText(item.Title);
            holder.pubDate.setText(item.PubDate);

            return convertView;
        }

        private class ViewHolder {
            final TextView pubDate;
            final TextView title;

            public ViewHolder(View view) {
                title = (TextView) view.findViewById(android.R.id.text1);
                pubDate = (TextView) view.findViewById(android.R.id.text2);
                view.setTag(this);
            }
        }
    }
}
