//  Copyright 2011 Jonathan Steele
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.itnoles.shared;

/**
 * It is one of the Modal object that shared between Controller and View.
 * @author Jonathan Steele
 */
public class News
{
	/**
	 * The member variable to hold String for title.
	 */
	private String mTitle;

	/**
	 * The member variable to hold String for link.
	 */
	private String mLink;

	/**
	 * The member variable to hold String for pubDate.
	 */
	private String mPubDate;

	/**
	 * The member variable to hold String for dese.
	 */
	private String mDesc;

	/**
	 * get value from mTitle.
	 * @return mTitle
	 */
	public String getTitle()
	{
		return mTitle;
	}

	/**
	 * set value to mTitle.
	 * @param title string for title
	 */
	public void setTitle(String title)
	{
		mTitle = title;
	}

	/**
	 * get value from mLink.
	 * @return mLink
	 */
	public String getLink()
	{
		return mLink;
	}

	/**
	 * set value to mLink.
	 * @param link string for link
	 */
	public void setLink(String link)
	{
		mLink = link;
	}

	/**
	 * set value to mPubDate.
	 * @param pubDate string for pubDate
	 */
	public void setPubDate(String pubDate)
	{
		mPubDate = pubDate;
	}

	/**
	 * get value from mPubDate.
	 * @return mPubDate
	 */
	public String getPubDate()
	{
		return mPubDate;
	}

	/**
	 * set value to mDesc..
	 * @param desc string for desc
	 */
	public void setDesc(String desc)
	{
		mDesc = desc;
	}

	/**
	 * get value from mDesc.
	 * @return mDesc
	 */
	public String getDesc()
	{
		return mDesc;
	}
}
