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

package com.itnoles.shared.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.itnoles.shared.R;
import com.itnoles.shared.activities.ScheduleActivity;
import com.itnoles.shared.activities.StaffActivity;

public class TeamFragment extends SherlockListFragment {
    private boolean mDualPane;
    private int mShownCheckPosition = -1;

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null && detailsFrame.getVisibility() != View.VISIBLE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        final String[] array = new String[] {getString(R.string.schedules), getString(R.string.staff)};
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_list_item_1, array);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        switch(position) {
            case 0:
                final ScheduleFragment schedule = new ScheduleFragment();
                replaceFragmentOrStartActivity(schedule, ScheduleActivity.class);
                break;
            case 1:
                final StaffFragment staff = new StaffFragment();
                replaceFragmentOrStartActivity(staff, StaffActivity.class);
                break;
            default:
        }
    }

    private void replaceFragmentOrStartActivity(SherlockListFragment fragment, Class clzz) {
        if (mDualPane) {
            final int position = getSelectedItemPosition();
            if (mShownCheckPosition != position) {
                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_details, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
                mShownCheckPosition = position;
            }
        } else {
            final Intent intent = new Intent(getActivity(), clzz);
            startActivity(intent);
        }
    }
}