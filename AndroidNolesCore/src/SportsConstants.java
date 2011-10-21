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

package com.itnoles.shared;

public interface SportsConstants
{
    /**
     * Worksheet Table name
     */
    String SCHEDULE = "schedule";
    String LINK = "link";
    String STAFF = "staff";

    // Turn off when deploying your app.
    boolean DEVELOPER_MODE = true;

    String SP_KEY_NEWS_TITLE = "SP_KEY_NEWS_TITLE";
    String SP_KEY_NEWS_URL = "SP_KEY_NEWS_URL";

    // ATOM Tags
    String ENTRY = "entry";

    String UPDATED = "updated";
    String CONTENT_AUTHORITY = "com.itnoles.shared.provider.sports";

    boolean SUPPORTS_FROYO = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO; //>= 2.2
    boolean SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD; //>= 2.3
    boolean SUPPORTS_HONEYCOMB = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB; //>= 3.0
}