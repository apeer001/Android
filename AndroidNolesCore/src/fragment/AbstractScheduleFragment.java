/*
 * Copyright (C) 2012 Jonathan Steele
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

package com.itnoles.shared.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;

import com.actionbarsherlock.app.SherlockListFragment;
import com.itnoles.shared.R;
import com.itnoles.shared.SimpleSectionedListAdapter;

import java.util.*;

public abstract class AbstractScheduleFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int SCHEDULE_LOADER = 0x1;

    private SimpleCursorAdapter mSimpleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The SimpleCursorAdapter is wrapped in a SimpleSectionedListAdapter so that
        // we can show list headers separating out the important sporting events.
        final String[] projection = {"tv", "date", "school", "time"};
        mSimpleAdapter = new SimpleCursorAdapter(getActivity(), R.layout.schedule_item, null, projection,
            new int[] {R.id.tv, R.id.date, R.id.school, R.id.time}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(new SimpleSectionedListAdapter(getActivity(), R.layout.list_section_header, mSimpleAdapter));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        final View detailsFrame = getActivity().findViewById(R.id.fragment_details);
        if (detailsFrame != null) {
            detailsFrame.setVisibility(View.GONE);
        }

        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = {"_id", "date", "tv", "time", "school", "sectiontitle"};
        return new CursorLoader(getActivity(), getURI(), projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        final List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            final String sectionTitle = cursor.getString(cursor.getColumnIndex("sectiontitle"));
            if (sectionTitle != null && !sectionTitle.isEmpty()) {
                sections.add(new SimpleSectionedListAdapter.Section(cursor.getPosition(), sectionTitle));
            }
            cursor.moveToNext();
        }

        mSimpleAdapter.swapCursor(cursor);

        final SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections.size()];
        ((SimpleSectionedListAdapter) getListAdapter()).setSections(sections.toArray(dummy));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSimpleAdapter.swapCursor(null);
    }

    protected abstract Uri getURI();
}