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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.itnoles.shared.R;
import com.itnoles.shared.service.SyncService;
import com.itnoles.shared.util.FragmentUtils;
import com.itnoles.shared.util.Lists;
import com.itnoles.shared.util.Maps;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ScheduleFragment extends ListFragment
{
    private static final String LOG_TAG = "ScheduleFragment";
    private static HttpClient sHttpClient;

    private boolean mDualPane;
    private int mShownCheckPosition = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.details);
        // If users click in non-dual pane tabs,
        // it cause this one to be gone too.
        if (detailsFrame != null && detailsFrame.getVisibility() == View.GONE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }

        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        new ScheduleLoadTask().execute(getString(R.string.schedule_url));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        FragmentUtils.dispatchPause(mDualPane, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        final HashMap fullObjects = (HashMap) getListAdapter().getItem(position);
        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(position, true);

            if (mShownCheckPosition != position) {
                // If we are not currently showing a fragment for the new
                // position, we need to create and install a new one.
                final ScheduleDetailsFragment df = ScheduleDetailsFragment.newInstance(fullObjects);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentUtils.replaceDetails(this, df);
                mShownCheckPosition = position;
            }
        }
        else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            final Intent intent = new Intent(getActivity(), ScheduleDetailsActivity.class);
            intent.putExtra("object", fullObjects);
            startActivity(intent);
        }
    }

    private static synchronized HttpClient getHttpClient(Context context)
    {
        if (sHttpClient == null) {
            sHttpClient = SyncService.getHttpClient(context);
        }
        return sHttpClient;
    }

    private class ScheduleLoadTask extends AsyncTask<String, Void, List<HashMap<String, String>>>
    {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... params)
        {
            final String param = params[0];
            final List<HashMap<String, String>> mEntries = Lists.newArrayList();
            final HttpClient httpClient = getHttpClient(getActivity());
            final HttpGet request = new HttpGet(param);
            try {
                final HttpResponse resp = httpClient.execute(request);
                final int status = resp.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    return null;
                }

                final HttpEntity entity = resp.getEntity();
                final String respString = EntityUtils.toString(entity);
                final JSONArray jsonArray = new JSONArray(respString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    final HashMap<String, String> map = Maps.newHashMap();
                    final JSONObject rec = jsonArray.getJSONObject(i);
                    final Iterator iter = rec.keys();
                    while (iter.hasNext()) {
                        final String key = (String) iter.next();
                        final String value = rec.getString(key);
                        map.put(key, value);
                    }
                    mEntries.add(map);
                }
            }
            catch (JSONException e) {
                Log.w(LOG_TAG, "Malformed response for json parsing", e);
            }
            catch (IOException e) {
                Log.w(LOG_TAG, "Problem reading remote response", e);
            }
            return mEntries;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result)
        {
            setListAdapter(new SimpleAdapter(getActivity(), result,
                android.R.layout.simple_list_item_1, new String[] {"school"},
                new int[] {android.R.id.text1}));
        }
    }
}