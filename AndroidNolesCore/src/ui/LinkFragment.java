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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.itnoles.shared.provider.ScheduleContract.Link;

public class LinkFragment extends ContentAwareFragment {
    private static final int LINK_LOADER = 0x02;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final String[] projection = {Link.NAME};
        getLoaderManager().initLoader(LINK_LOADER, null, this);
        setCursorAdapter(android.R.layout.simple_list_item_1, projection, new int[] {android.R.id.text1});
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = {"_id", Link.NAME, Link.URL, };
        return new CursorLoader(getActivity(), Link.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Cursor cursor = getCursorFromLoader();
        cursor.moveToPosition(position);
        final String urlString = cursor.getString(cursor.getColumnIndex(Link.URL));
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}