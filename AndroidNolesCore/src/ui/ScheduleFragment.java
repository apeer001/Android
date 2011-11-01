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

import com.androidquery.AQuery;
import com.itnoles.shared.R;
import com.itnoles.shared.provider.ScheduleContract.Schedule;
import com.itnoles.shared.util.Lists;

import java.util.List;

public class ScheduleFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int SCHEDULE_LOADER = 0x01;
    private static final String[] PROJECTION = {Schedule.DATE, Schedule.TIME, Schedule.SCHOOL};

    private SimpleCursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        final AQuery aq = new AQuery(getActivity());
        aq.id(R.id.details).gone();

        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);

        mAdapter = new SimpleCursorAdapter(getActivity(),
            R.layout.schedule_item, null, PROJECTION,
            new int[] {R.id.date, R.id.time, R.id.school},
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        final List<String> projectionList = Lists.newArrayList();
        projectionList.add("_id");
        for (String projection : PROJECTION) {
            projectionList.add(projection);
        }
        final String[] newProjection = projectionList.toArray(new String[projectionList.size()]);
        return new CursorLoader(getActivity(), Schedule.CONTENT_URI, newProjection, null, null, null);
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