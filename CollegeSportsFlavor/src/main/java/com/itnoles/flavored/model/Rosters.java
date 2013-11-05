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

package com.itnoles.flavored.model;

public class Rosters {
    private String details;
    private String bioId;

    public String firstName;
    public String lastName;
    public String position;

    public void setValue(String key, String value) {
        switch (key) {
            case "bio_id":
                bioId = value;
                break;
            case "details":
                details = value;
                break;
            case "first_name":
                firstName = value;
                break;
            case "last_name":
                lastName = value;
                break;
            case "position":
                position = value;
                break;
            default:
        }
    }

    public String getFullURL() {
        return details + '/' + bioId + ".xml";
    }

    public String getFirstAndLastName() {
        return firstName.concat(" ").concat(lastName);
    }

    @Override
    public String toString() { return lastName; }
}