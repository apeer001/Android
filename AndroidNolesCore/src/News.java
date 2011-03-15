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

public class News {
	private String mTitle;
	private String mLink;
	private String mPubDate;
	private String mImageURL;
	private String mDesc;
	
	// getters and setters omitted for brevity
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public void setPubDate(String pubDate) {
		mPubDate = pubDate;
	}
	
	public String getPubDate() {
		return mPubDate;
	}
	
	public void setImageURL(String imageURL) {
		mImageURL = imageURL;
	}
	
	public String getImageURL() {
		return mImageURL;
	}
	
	public void setDesc(String desc) {
		mDesc = desc;
	}
	
	public String getDesc() {
		return mDesc;
	}
}