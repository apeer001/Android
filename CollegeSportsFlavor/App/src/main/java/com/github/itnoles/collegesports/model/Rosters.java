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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.itnoles.collegesports.model;

public final class Rosters {
    private String details;
    private String bioId;
    private String mFirstName;
    private String mLastName;
    private String mPosition;

    public void setValue(String key, String value) {
        switch (key) {
            case "bio_id":
                bioId = value;
                break;
            case "details":
                details = value;
                break;
            case "first_name":
                mFirstName = value;
                break;
            case "last_name":
                mLastName = value;
                break;
            case "position":
                mPosition = value;
                break;
            default:
        }
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPosition() {
        return mPosition;
    }

    public String getFullURL() {
        return details + '/' + bioId + ".xml";
    }

    public String getFirstAndLastName() {
        return mFirstName.concat(" ").concat(mLastName);
    }

    @Override
    public String toString() { return mLastName; }
}