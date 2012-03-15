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

package com.itnoles.shared.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.itnoles.shared.SportsConstants;

public final class ScheduleContract {
    private ScheduleContract() {
    }

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + SportsConstants.CONTENT_AUTHORITY);

    public static final class Schedule implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SportsConstants.SCHEDULE).build();
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String SCHOOL = "school";
        public static final String LOCATION = "location";

        private Schedule() {
        }

        public static Uri buildScheduleUri(String scheduleId) {
            return CONTENT_URI.buildUpon().appendPath(scheduleId).build();
        }
    }

    public static final class Link implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SportsConstants.LINK).build();
        public static final String NAME = "name";
        public static final String URL = "url";

        private Link() {
        }

        public static Uri buildLinkUri(String linkId) {
            return CONTENT_URI.buildUpon().appendPath(linkId).build();
        }
    }

    public static final class Staff implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SportsConstants.STAFF).build();
        public static final String NAME = "name";
        public static final String POSITIONS = "positions";

        private Staff() {
        }

        public static Uri buildStaffUri(String staffId) {
            return CONTENT_URI.buildUpon().appendPath(staffId).build();
        }
    }
}