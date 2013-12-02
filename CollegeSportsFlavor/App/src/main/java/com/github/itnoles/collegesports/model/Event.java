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

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class Event {
    private static final String LOG_TAG = "Event";

    private final String date;
    private String mHomeTeam;
    private String mAwayTeam;
    private String mHomeScore;
    private String mAwayScore;
    private String mTime;

    public Event(String dateString) {
        date = dateString;
    }

    public void setHomeTeam(String hometeam) {
        mHomeTeam = hometeam;
    }

    public String getHomeTeam() {
        return mHomeTeam;
    }

    public void setHomeScore(String homescore) {
        mHomeScore = homescore;
    }

    public String getHomeScore() {
        return mHomeScore;
    }

    public void setAwayTeam(String awayteam) {
        mAwayTeam = awayteam;
    }

    public String getAwayTeam() {
        return mAwayTeam;
    }

    public void setAwayScore(String awayscore) {
        mAwayScore = awayscore;
    }

    public String getAwayScore() {
        return mAwayScore;
    }

    public void setTime(String timeString) {
    	Date time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        try {
            time = sdf.parse(date.concat(timeString));
        } catch (ParseException pe) {
            Log.w(LOG_TAG, "Can't parse this date", pe);
        }

        if (time == null) {
        	return;
        }

        sdf.applyPattern("MMM dd 'at' hh:mma");
        mTime = sdf.format(time);
    }

    public String getTime() {
        return mTime;
    }
}