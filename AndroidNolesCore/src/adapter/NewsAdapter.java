//  Copyright 2010 Jonathan Steele
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
package com.itnoles.shared.adapter;

import android.content.Context;
import android.view.*; // LayoutInflater, View and ViewGroup
import android.widget.*; // ArrayAdapter, ImageView and TextView

import java.util.List;

import com.itnoles.shared.*; //ImageDownloader and News

/**
 * NewsAdapter
 * bind News and ArrayAdapter together
 * @author Jonathan Steele
 */

public class NewsAdapter extends ArrayAdapter<News> {
	private LayoutInflater inflator;
	
	// Constructor
	public NewsAdapter(Context context, List<News> news) {
		super(context, R.id.text1, news);
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		News news = super.getItem(position);
		if (news == null)
			return convertView;
			
		View v = convertView;
		if (v == null) {
			if (news.getImageURL() != null && news.getImageURL().length() > 0)
				v = inflator.inflate(R.layout.headlines_row_icon, null);
			else
				v = inflator.inflate(R.layout.headlines_row, null);
		}
		
		ImageView thumbnail = (ImageView) v.findViewById(R.id.icon);
		if (thumbnail != null) {
			ImageDownloader imageDownloader = new ImageDownloader();
			imageDownloader.download(news.getImageURL(), thumbnail);
		}
		
		TextView title = (TextView) v.findViewById(R.id.text1);
		if (title != null)
			title.setText(news.getTitle());
			
		TextView subTitle = (TextView) v.findViewById(R.id.text2);
		if (subTitle != null)
			subTitle.setText(news.getPubdate());
		return v;
	}
}