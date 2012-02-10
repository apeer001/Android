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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.itnoles.shared.provider.ScheduleContract.Staff;

public class StaffFragment extends ContentAwareFragment {
    private static final int STAFF_LOADER = 0x03;
    private static final String[] PROJECTION = {Staff.NAME, Staff.POSITIONS};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(STAFF_LOADER, null, this);
        setCursorAdapter(android.R.layout.simple_list_item_2, PROJECTION, new int[] {android.R.id.text1, android.R.id.text2});
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] newProjection = getNewProjectionList(PROJECTION);
        return new CursorLoader(getActivity(), Staff.CONTENT_URI, newProjection, null, null, null);
    }
}