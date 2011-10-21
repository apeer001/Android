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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itnoles.shared.R;

public abstract class AbstractTeamFragment extends ListFragment
{
    private boolean mDualPane;
    private int mShownCheckPosition = -1;

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        super.onActivityCreated(savedState);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.details);
        // If users click in non-dual pane tabs,
        // it cause this one to be gone too.
        if (detailsFrame != null && detailsFrame.getVisibility() == View.GONE) {
            detailsFrame.setVisibility(View.VISIBLE);
        }
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        final ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), resourceID(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        switch(position) {
        case 0:
            final ScheduleFragment schedule = new ScheduleFragment();
            replaceFragmentInFrame(schedule);
            break;
        case 1:
            final StaffFragment staff = new StaffFragment();
            replaceFragmentInFrame(staff);
            break;
        default:
        }
    }

    private void replaceFragmentInFrame(Fragment fragment)
    {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mDualPane) {
            final int position = getSelectedItemPosition();
            if (mShownCheckPosition != position) {
                ft.replace(R.id.details, fragment).commit();
                mShownCheckPosition = position;
            }
        }
        else {
            ft.addToBackStack("team").replace(R.id.titles, fragment).commit();
        }
    }

    public abstract int resourceID();
}