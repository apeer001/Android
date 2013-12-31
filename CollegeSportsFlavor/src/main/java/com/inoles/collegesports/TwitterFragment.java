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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//import java.lang.reflect.Type;

/**
 * A fragment representing a list of Twitter Items.
 */
public class TwitterFragment extends ListFragment {
    static class Tweet {
        Tweet retweeted_status;
        User user;
        String text;
        String id_str;
    }

    static class User {
        String screen_name;
        String profile_image_url;
    }

    /*static class DateDeserializer implements JsonDeserializer<Tweet> {
        @Override
        public Tweet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return null;
        }
    }*/

    private TweetListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_with_empty_container, container, true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new TweetListAdapter(getActivity());
        setListAdapter(mAdapter);

        getCredentials();
    }

    // This "Future" tracks loading operations.
    // A Future is an object that manages the state of an operation
    // in progress that will have a "Future" result.
    // You can attach callbacks (setCallback) for when the result is ready,
    // or cancel() it if you no longer need the result.
    Future<List<Tweet>> loading;

    String accessToken;
    private void getCredentials() {
        Ion.with(getActivity(), "https://api.twitter.com/oauth2/token")
           .basicAuthentication(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET)
           .setBodyParameter("grant_type", "client_credentials")
           .asJsonObject()
           .setCallback(new FutureCallback<JsonObject>() {
               @Override
               public void onCompleted(Exception e, JsonObject result) {
                   if (e != null && getActivity() != null) {
                       Toast.makeText(getActivity(), "Error loading tweets", Toast.LENGTH_LONG).show();
                       return;
                   }
                   accessToken = result.get("access_token").getAsString();
                   load();
               }
           });
    }

    private void load() {
        // don't attempt to load more if a load is already in progress
        if (loading != null && !loading.isDone() && !loading.isCancelled()) {
            return;
        }

        // load the tweets
        String url = BuildConfig.TWITTER_URL;
        if (mAdapter.getCount() > 0) {
            // load from the "last" id
            Tweet last = mAdapter.getItem(mAdapter.getCount() - 1);
            url += "&max_id=" + last.id_str;
        }

        // This request loads a URL as JsonArray and invokes
        // a callback on completion.
        loading = Ion.with(getActivity(), url)
                .setHeader("Authorization", "Bearer " + accessToken)
                .as(new TypeToken<List<Tweet>>() { })
                .setCallback(new FutureCallback<List<Tweet>>() {
                    @Override
                    public void onCompleted(Exception e, List<Tweet> result) {
                        // this is called back onto the ui thread,
                        // no Activity.runOnUiThread or Handler.post necessary.
                        if (e != null && getActivity() != null) {
                            Toast.makeText(getActivity(), "Error loading tweets", Toast.LENGTH_LONG).show();
                            return;
                        }
                        // add the tweets
                        mAdapter.addAll(result);
                    }
                });
    }

    class TweetListAdapter extends ArrayAdapter<Tweet> {
        private LayoutInflater mInflater;

        public TweetListAdapter(Context context) {
            super(context, 0);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Nullable
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                mInflater.inflate(R.layout.tweet, parent, false);

                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.handle = (TextView) convertView.findViewById(android.R.id.text1);
                holder.text = (TextView) convertView.findViewById(android.R.id.text2);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // we're near the end of the list adapter, so load more items
            if (position >= getCount() - 3) {
                load();
            }

            // grab the tweet (or retweet)
            Tweet tweet = getItem(position);
            Tweet retweet = tweet.retweeted_status;
            if (retweet != null) {
                tweet = retweet;
            }

            Ion.with(holder.icon).load(tweet.user.profile_image_url);

            // and finally, set the name and text
            holder.handle.setText(tweet.user.screen_name);
            holder.text.setText(tweet.text);
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView icon;
        TextView handle;
        TextView text;
    }
}
