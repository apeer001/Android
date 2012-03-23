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

import com.itnoles.shared.R;
import com.itnoles.shared.provider.ScheduleContract.Schedule;

public class ScheduleFragment extends ContentAwareFragment {
    private static final int SCHEDULE_LOADER = 0x01;
    private static final String[] PROJECTION = {Schedule.DATE, Schedule.TIME, Schedule.SCHOOL, Schedule.LOCATION};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
        setCursorAdapter(R.layout.schedule_item, PROJECTION, new int[] {R.id.date, R.id.time, R.id.school, R.id.location});
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] newProjection = getNewProjectionList(PROJECTION);
        return new CursorLoader(getActivity(), Schedule.CONTENT_URI, newProjection, null, null, null);
    }
}