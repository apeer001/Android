/*
 * Copyright (c) 2013 Jonathan Steele
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.inoles.collegesports;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends ListFragment {
    static class Gallery {
        String ImageURL;
        String Title;
        String GalleryXML;
        String Date;

        void setValue(String key, String value) {
            switch (key) {
                case "title":
                    Title = value;
                    break;
                case "gallery_xml":
                    GalleryXML = value;
                    break;
                case "thumb_url":
                    ImageURL = value;
                    break;
                case "create_date":
                    Date = value;
                    break;
                default:
            }
        }
    }

    private static final String LOG_TAG = "GalleryFragment";

    private GalleryListAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() == null) {
            return;
        }

        mAdapter = new GalleryListAdapter(getActivity());
        setListAdapter(mAdapter);

        View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null) {
            detailsFrame.setVisibility(View.GONE);
        }

        Ion.with(getActivity(), BuildConfig.GALLERY_URL).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String s) {
                load(s);
            }
        });
    }

    private void load(String xmlString) {
        StringReader sr = new StringReader(xmlString);
        try {
            XmlPullParser parser = ParserUtils.newPullParser(sr);
            List<Gallery> results = new ArrayList<>();
            // The Gallery that is currently being parsed
            Gallery currentGallery = null;
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if ("gallery".equals(name)) {
                        currentGallery = new Gallery();
                    } else if (currentGallery != null) {
                        currentGallery.setValue(name, parser.nextText());
                    }
                } else if (parser.getEventType() == XmlPullParser.END_TAG
                        && "gallery".equals(name)) {
                    results.add(currentGallery);
                }
            }
            mAdapter.addAll(results);
        } catch (IOException | XmlPullParserException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            sr.close();
        }
    }

    class GalleryListAdapter extends ArrayAdapter<Gallery> {
        private LayoutInflater mInflater;

        public GalleryListAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.simple_list_item_3, parent, false);

                holder = new ViewHolder();
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Gallery item = getItem(position);

            ImageView iv = (ImageView) convertView.findViewById(R.id.thumbnail);
            Ion.with(iv).load(item.ImageURL);

            holder.date.setText(item.Date);
            holder.title.setText(item.Title);

            return convertView;
        }
    }

    private class ViewHolder {
        TextView date;
        TextView title;
    }
}
