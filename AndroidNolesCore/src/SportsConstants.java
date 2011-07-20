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
    String CONTENT_AUTHORITY = "com.itnoles.shared";

    boolean SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
    boolean SUPPORTS_HONEYCOMB = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
    boolean SUPPORTS_FROYO = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO;
}