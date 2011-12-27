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
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import com.itnoles.shared.R;
import com.itnoles.shared.util.AQuery;
import com.itnoles.shared.util.Lists;

import java.util.List;

public abstract class ContentAwareFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter mAdapter;

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final AQuery aq = new AQuery(getActivity());
        aq.id(R.id.details).gone();
    }

    protected String[] getNewProjectionList(String[] listProjection) {
    	final List<String> projectionList = Lists.newArrayList();
        projectionList.add("_id");
        for (String projection : listProjection) {
            projectionList.add(projection);
        }
        return projectionList.toArray(new String[projectionList.size()]);
    }

	protected void setCursorAdapter(int layout, String[] from, int[] to) {
		mAdapter = new SimpleCursorAdapter(getActivity(), layout, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(mAdapter);
	}

    protected Cursor getCursorFromLoader() {
        return mAdapter.getCursor();
    }

	@Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}