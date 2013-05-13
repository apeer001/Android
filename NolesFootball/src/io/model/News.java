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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.nolesfootball.io.model;

import com.itnoles.nolesfootball.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * It is one of the Modal object that shared between Controller and View.
 */
public class News {
    private static final String LOG_TAG = LogUtils.makeLogTag(News.class);

    public String title;
    public String link;

    private String mPubDate;
    private Date mPublished;

    public void setValue(String key, String value) {
        if ("title".equals(key)) {
            title = value;
        } else if ("pubDate".equals(key)) {
            mPubDate = value;
        } else if ("published".equals(key)) {
            setPublished(value);
        } else if ("link".equals(key)) {
            link = value;
        }
    }

    private void setPublished(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            mPublished = sdf.parse(date);
        } catch (ParseException e) {
            LogUtils.LOGW(LOG_TAG, "Fail to parse published date", e);
        }
    }

    public String getPubDate() {
        if (mPublished != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.US);
            return sdf.format(mPublished);
        }
        return mPubDate;
    }
}