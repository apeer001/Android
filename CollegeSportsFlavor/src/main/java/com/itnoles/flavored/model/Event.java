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

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Event {
    private static final String LOG_TAG = "Event";

    private final String date;

    public String hn;
    public String vn;
    public String hs;
    public String vs;
    public Date fullDate;

    public Event(String dateString) {
        date = dateString;
    }

    public void setFullDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        try {
            fullDate = sdf.parse(date.concat(time));
        } catch (ParseException pe) {
            Log.w(LOG_TAG, "Can't parse this date", pe);
        }
    }
}