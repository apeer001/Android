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
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class RostersDetailFragment extends ListFragment {
    static class RosterDetail {
        String experience;
        String eligibility;
        String height;
        String weight;
        String position_event;
        String hometown;
    }

    private static final String LOG_TAG = "RostersDetailFragment";

    public static RostersDetailFragment newInstance(String urlString) {
        RostersDetailFragment f = new RostersDetailFragment();

        // Supply url input as an argument.
        Bundle args = new Bundle();
        args.putString("url", urlString);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() == null) {
            return;
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1);
        setListAdapter(adapter);

        if (getArguments() == null) {
            return;
        }

        Ion.with(getActivity(), getArguments().getString("url"))
           .as(RosterDetail.class)
           .setCallback(new FutureCallback<RosterDetail>() {
               @Override
               public void onCompleted(Exception e, RosterDetail result) {
                   // this is called back onto the ui thread, no Activity.runOnUiThread
                   // or Handler.post necessary.
                   if (e != null) {
                       Log.e(LOG_TAG, Log.getStackTraceString(e));
                       return;
                   }
                   adapter.add("Experience: " + result.experience);
                   adapter.add("Class: " + result.eligibility);
                   adapter.add("Height: " + result.height);
                   adapter.add("Weight: " + result.weight);
                   adapter.add("Hometown: " + result.hometown);
                   adapter.add(result.position_event.replace("=>", ": "));
               }
           });
    }
}