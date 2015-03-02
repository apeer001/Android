/*
 * Copyright (C) 2015 Jonathan Steele
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

package com.inoles.nolesfootball.model;

import java.util.Comparator;

public class Rosters {
    public String mFirstName;
    public String mLastName;
    public String mPosition;
    public int mIsCoach;
    public String mShirtNumber;

    public Rosters copy() {
        Rosters copy = new Rosters();
        copy.mFirstName = mFirstName;
        copy.mLastName = mLastName;
        copy.mPosition = mPosition;
        copy.mIsCoach = mIsCoach;
        copy.mShirtNumber = mShirtNumber;
        return copy;
    }

    public static final Comparator<Rosters> NAME = new Comparator<Rosters>() {
        @Override
        public int compare(Rosters rosters, Rosters rosters2) {
            if ("Fisher".equals(rosters2.mLastName)) {
                return 0;
            }
            return rosters.mLastName.compareTo(rosters2.mLastName);
        }
    };

    public static final Comparator<Rosters> NUMBER = new Comparator<Rosters>() {
        @Override
        public int compare(Rosters rosters, Rosters rosters2) {
            return rosters.mShirtNumber.compareTo(rosters2.mShirtNumber);
        }
    };
}
