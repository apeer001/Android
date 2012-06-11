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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.nolesfootball.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.itnoles.nolesfootball.ScheduleProvider;
import com.itnoles.shared.fragment.AbstractScheduleFragment;

public class ScheduleFragment extends AbstractScheduleFragment {
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = {"_id", "date", "time", "school"};
        return new CursorLoader(getActivity(), ScheduleProvider.SCHEDULE_CONTENT_URI, projection, null, null, null);
    }
}