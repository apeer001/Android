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

import android.view.View;
import android.widget.ListView;

import com.itnoles.shared.fragment.AbstractTeamFragment;

public class TeamFragment extends AbstractTeamFragment {
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        switch(position) {
            case 0:
                final ScheduleFragment schedule = new ScheduleFragment();
                replaceFragmentOrStartActivity(schedule, ScheduleActivity.class);
                break;
            case 1:
                final StaffFragment staff = new StaffFragment();
                replaceFragmentOrStartActivity(staff, StaffActivity.class);
                break;
            default:
        }
    }
}