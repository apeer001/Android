/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itnoles.shared.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.itnoles.shared.SportsConstants;

public class ScheduleContract
{
    private ScheduleContract()
    {
    }

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + SportsConstants.CONTENT_AUTHORITY);

    public static class Schedule implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SportsConstants.SCHEDULE).build();
        public static final String SCHEDULE_ID = "schedule_id";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String SCHOOL = "school";

        public static Uri buildScheduleUri(String scheduleId)
        {
            return CONTENT_URI.buildUpon().appendPath(scheduleId).build();
        }
    }

    public static class Link implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SportsConstants.LINK).build();
        public static final String LINK_ID = "link_id";
        public static final String NAME = "name";
        public static final String URL = "url";

        public static Uri buildLinkUri(String linkId)
        {
            return CONTENT_URI.buildUpon().appendPath(linkId).build();
        }
    }

    public static class Staff implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SportsConstants.STAFF).build();
        public static final String STAFF_ID = "staff_id";
        public static final String NAME = "name";
        public static final String POSITIONS = "positions";

        public static Uri buildStaffUri(String staffId)
        {
            return CONTENT_URI.buildUpon().appendPath(staffId).build();
        }
    }
}