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

package com.itnoles.nolesfootball;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itnoles.shared.fragment.AbstractLinkFragment;

public class LinkFragment extends AbstractLinkFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setListAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.linkNames, android.R.layout.simple_list_item_1));
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final String urlString = getResources().getStringArray(R.array.linkValues)[position];
        fromItemClick(urlString, position);
    }
}