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

public final class SportsConstants {
    private SportsConstants() {}

    /**
     * Worksheet Table name
     */
    public static String SCHEDULE = "schedule";
    public static String LINK = "link";
    public static String STAFF = "staff";

    // it use Ant to replace this value
    public static boolean DEVELOPER_MODE = true;

    /**
     * These values are constants used for shared preferences.
     * You shouldn't need to modify them.
     */
    public static String SP_KEY_NEWS_TITLE = "SP_KEY_NEWS_TITLE";
    public static String SP_KEY_NEWS_URL = "SP_KEY_NEWS_URL";
    public static String SP_KEY_NEWS_REFRESH = "SP_KEY_NEWS_REFRESH";

    // ATOM Tags
    public static String ENTRY = "entry";
    public static String UPDATED = "updated";

    public static String CONTENT_AUTHORITY = "com.itnoles.shared.provider.sports";

    public static boolean SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
    public static boolean SUPPORTS_HONEYCOMB = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
}