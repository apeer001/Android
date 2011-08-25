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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;

import com.itnoles.shared.R;
import com.itnoles.shared.provider.ScheduleContract.Schedule;

public class ScheduleFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int SCHEDULE_LOADER = 0x01;
    private static final String LOG_TAG = "ScheduleFragment";

    private SimpleCursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        final View detailFrame = getActivity().findViewById(R.id.details);
        if (detailFrame != null) {
            detailFrame.setVisibility(View.GONE);
        }

        final String[] projection = {Schedule.SCHOOL, Schedule.TIME};

        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);

        mAdapter = new SimpleCursorAdapter(getActivity(),
            android.R.layout.simple_list_item_2, null, projection,
            new int[] {android.R.id.text1, android.R.id.text2},
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        final String[] projection = {"_id", Schedule.DATE, Schedule.TIME, Schedule.SCHOOL};
        return new CursorLoader(getActivity(), Schedule.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mAdapter.swapCursor(null);
    }
}