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

package com.itnoles.shared.util;

/**
 * It is one of the Modal object that shared between Controller and View.
 */
public class News
{
	private String mTitle;
	private String mLink;
	private String mPubDate;
	private String mDesc;

	public void setValue(String key, String value)
	{
	    if ("title".equals(key)) {
	        mTitle = value;
	    }
	    else if ("pubDate".equals(key) || "published".equals(key)) {
	        mPubDate = value;
	    }
	    else if ("description".equals(key) || "content".equals(key)) {
	        mDesc = value;
	    }
    }

    public String getTitle()
    {
        return mTitle;
    }

	public String getLink()
	{
		return mLink;
	}

	public void setLink(String link)
	{
		mLink = link;
	}

	public String getPubDate()
	{
		return mPubDate;
	}

	public String getDesc()
	{
		return mDesc;
	}
}