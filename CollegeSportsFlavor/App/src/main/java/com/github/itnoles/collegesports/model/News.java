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

import java.util.regex.Pattern;

public final class News {
    private static final Pattern COMPILE = Pattern.compile("<.*>");

    private String mTitle;
    private String mLink;
    private String mDesc;
    private String mPubDate;
    //private String mImageURL;

    public void setValue(String key, String value) {
        switch (key) {
            case "title":
                mTitle = value;
                break;
            case "pubDate":
                mPubDate = value;
                break;
            case "link":
                mLink = value;
                break;
            case "description":
                mDesc = COMPILE.matcher(value).replaceAll("");
                break;
            /*case "enclosure":
                mImageURL = value;
                break;*/
            default:
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getPubDate() {
        return mPubDate;
    }

}