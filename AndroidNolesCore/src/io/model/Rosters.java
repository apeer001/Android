/*
 * Copyright (C) 2013 Jonathan Steele
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

package com.itnoles.shared.io.model;

public class Rosters {
    private String mFirstName;
    private String mLastName;
    private String mPosition;
    private boolean mStaff;

    public void setValue(String key, String value) {
        if ("first_name".equals(key)) {
            mFirstName = value;
        } else if ("last_name".equals(key)) {
            mLastName = value;
        } else if ("position".equals(key)) {
            mPosition = value;
        }
    }

    public String getFullName() { return mLastName + ", " + mFirstName; }
    public String getPosition() { return mPosition; }

    public void setStaff(boolean staff) { mStaff = staff; }
    public boolean getStaff() { return mStaff; }
}