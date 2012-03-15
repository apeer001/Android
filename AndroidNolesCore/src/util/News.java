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

package com.itnoles.shared.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * It is one of the Modal object that shared between Controller and View.
 */
public class News {
    private static final String LOG_TAG = "News";

    private String mTitle;
    private String mLink;
    private String mPubDate;
    private String mDesc;
    private Date mPublished;

    private final ThreadLocal<SimpleDateFormat> threadDate = new ThreadLocal<SimpleDateFormat>();
    private SimpleDateFormat getDateFormat(String format) {
        SimpleDateFormat sdf = threadDate.get();
        if (sdf == null) {
            sdf = new SimpleDateFormat(format, Locale.US);
            threadDate.set(sdf);
        }
        return sdf;
    }

    public void setValue(String key, String value) {
        if ("title".equals(key)) {
            mTitle = value;
        } else if ("pubDate".equals(key)) {
            mPubDate = value;
        } else if ("published".equals(key)) {
            setPublished(value);
        } else if ("description".equals(key) || "content".equals(key)) {
            mDesc = value;
        } else if ("link".equals(key)) {
            mLink = value;
        }
    }

    private void setPublished(String date) {
        try {
            mPublished = getDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date);
        } catch (ParseException e) {
            Log.w(LOG_TAG, "Fail to parse published date", e);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public String getPubDate() {
        if (mPublished != null) {
            return getDateFormat("E, dd MMM yyyy HH:mm:ss z").format(mPublished);
        }
        return mPubDate;
    }

    public String getDesc() {
        return mDesc;
    }
}