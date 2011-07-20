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
import com.itnoles.shared.provider.ScheduleContract.Staff;

public class StaffFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int STAFF_LOADER = 0x03;
    private SimpleCursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        final View detailFrame = getActivity().findViewById(R.id.details);
        if (detailFrame != null) {
            detailFrame.setVisibility(View.GONE);
        }

        final String[] projection = {Staff.NAME, Staff.POSITIONS, };

        getLoaderManager().initLoader(STAFF_LOADER, null, this);

        mAdapter = new SimpleCursorAdapter(getActivity(),
            android.R.layout.simple_list_item_2, null, projection,
            new int[] {android.R.id.text1, android.R.id.text2},
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        final String[] projection = {"_id", Staff.NAME, Staff.POSITIONS, };
        final CursorLoader cursorLoader = new CursorLoader(getActivity(), Staff.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
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