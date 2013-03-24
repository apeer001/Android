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

package com.itnoles.shared.io.model;

import android.text.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.itnoles.shared.LogUtils.makeLogTag;
import static com.itnoles.shared.LogUtils.LOGW;

/**
 * It is one of the Modal object that shared between Controller and View.
 */
public class News {
    private static final String LOG_TAG = makeLogTag(News.class);

    private String mTitle;
    private String mLink;
    private String mPubDate;
    private String mDesc;
    private Date mPublished;

    public void setValue(String key, String value) {
        if ("title".equals(key)) {
            mTitle = value;
        } else if ("pubDate".equals(key)) {
            mPubDate = value;
        } else if ("published".equals(key)) {
            setPublished(value);
        } else if ("description".equals(key) || "content".equals(key)) {
            mDesc = Html.fromHtml(value).toString();
        } else if ("link".equals(key)) {
            mLink = value;
        }
    }

    private void setPublished(String date) {
        try {
            final SafeAndroidDateFormat publishedDate = new SafeAndroidDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            mPublished = publishedDate.get().parse(date);
        } catch (ParseException e) {
            LOGW(LOG_TAG, "Fail to parse published date", e);
        }
    }

    public String getTitle() { return mTitle; }
    public String getLink() { return mLink; }

    public String getPubDate() {
        if (mPublished != null) {
            final SafeAndroidDateFormat date = new SafeAndroidDateFormat("E, dd MMM yyyy HH:mm:ss z");
            return date.get().format(mPublished);
        }
        return mPubDate;
    }

    public String getDesc() { return mDesc; }

    static class SafeAndroidDateFormat {
        private final ThreadLocal<SimpleDateFormat> mThreadDate;

        SafeAndroidDateFormat(String format) {
            mThreadDate = new ThreadLocal<SimpleDateFormat>();
            final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            mThreadDate.set(sdf);
        }

        public final SimpleDateFormat get() {
            return mThreadDate.get();
        }
    }
}