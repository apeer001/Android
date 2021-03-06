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

public class Rosters {
    public String mFirstName;
    public String mLastName;
    public String mPosition;
    public String mShirtNumber;

    public Rosters copy() {
        Rosters copy = new Rosters();
        copy.mFirstName = mFirstName;
        copy.mLastName = mLastName;
        copy.mPosition = mPosition;
        copy.mShirtNumber = mShirtNumber;
        return copy;
    }
}
