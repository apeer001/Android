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

import android.text.format.Time;

/**
 * It is one of the Modal object that shared between Controller and View.
 */
public class News {
	private String mTitle;
	private String mLink;
	private String mPubDate;
	private String mDesc;

	public void setValue(String key, String value) {
	    if ("title".equals(key)) {
	        mTitle = value;
	    } else if ("pubDate".equals(key)) {
	        mPubDate = value;
	    } else if ("published".equals(key)) {
	    	final Time time = new Time();
	    	time.parse3339(value);
	    	time.normalize(false);
	    	mPubDate = time.format("%a");
	    } else if ("description".equals(key) || "content".equals(key)) {
	        mDesc = value;
	    } else if ("link".equals(key)) {
	    	mLink = value;
	    }
    }

    public String getTitle() {
        return mTitle;
    }

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public String getPubDate() {
		return mPubDate;
	}

	public String getDesc() {
		return mDesc;
	}
}