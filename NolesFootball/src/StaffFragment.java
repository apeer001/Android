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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.nolesfootball;

import android.net.Uri;

import com.itnoles.shared.fragment.AbstractStaffFragment;

public class StaffFragment extends AbstractStaffFragment {
    @Override
    protected Uri getURI() {
        return ScheduleProvider.STAFF_CONTENT_URI;
    }

    @Override
    protected String getFullURL(String url) {
        return "http://www.seminoles.com/sports/m-footbl/mtt/" + url + "00.html";
    }
}