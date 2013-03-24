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

package com.itnoles.knightfootball;

import android.net.Uri;

import com.itnoles.shared.provider.AbstractScheduleProvider;

public class ScheduleProvider extends AbstractScheduleProvider {
    public static final String CONTENT_AUTHORITY = "com.itnoles.knightfootball.provider";
    public static final Uri SCHEDULE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + "/schedule");

    /**
     * Allocate the UriMatcher object that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    static {
        URIMATCHER.addURI(CONTENT_AUTHORITY, "schedule", SCHEDULE);
        URIMATCHER.addURI(CONTENT_AUTHORITY, "schedule/*", SCHEDULE_ID);
    }
}