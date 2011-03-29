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
package com.itnoles.shared;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParser;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Xml;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * FeedBackgroundTask
 * it is the class has a delegate for AsyncTask class which is for atom or rss parsing
 * @author Jonathan Steele
 */

public class FeedBackgroundTask extends AsyncTask<String, Void, List<News>> {
	private static final String LOG_TAG = "FeedBackgroundTask";
	private AsyncTaskCompleteListener<List<News>> callback;
	
	// names of the XML tags
	private static final String PUB_DATE = "pubDate";
	private static final String ENCOL = "enclosure";
	private static final String LINK = "link";
	private static final String DESCRIPTION = "description";
	private static final String TITLE = "title";
	private static final String ITEM = "item";
	private static final String ENTRY = "entry";
	private static final String PUBLISHED = "published";
	private static final String CONTENT = "content";
	
	// Constructor
	public FeedBackgroundTask(AsyncTaskCompleteListener<List<News>> callback) {
		this.callback = callback;
	}
	
	// perform a computation on a background thread
	@Override
	protected List<News> doInBackground(String... params)
	{
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(params[0]);
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(LOG_TAG, "Error " + statusCode + " while retrieving content from " + params[0]);
				return null;
			}
			
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = new BufferedInputStream(entity.getContent(), 1024);
					return parse(inputStream);
				} finally {
					if (inputStream != null)
						inputStream.close();
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
			Log.w(LOG_TAG, "bad feed parsing", e);
		} finally {
			if (client != null)
				client.close();
		}
		return null;
	}
	
	// Runs on the UI thread after doInBackground
	@Override
	protected void onPostExecute(List<News> result)
	{
		callback.onTaskComplete(result);
	}
	
	private List<News> parse(InputStream inputstream) throws Exception
	{
		List<News> news = null;
		XmlPullParser parser = Xml.newPullParser();
		// auto-detect the encoding from the stream
		parser.setInput(inputstream, null);
		int eventType = parser.getEventType();
		News currentNews = null;
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			String name = null;
			switch (eventType)
			{
				case XmlPullParser.START_DOCUMENT:
					news = new ArrayList<News>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(ITEM) || name.equalsIgnoreCase(ENTRY)) {
						currentNews = new News();
					} else if (currentNews != null) {
						if (name.equalsIgnoreCase(LINK)) {
							if (parser.getAttributeCount() > 0)
								currentNews.setLink(parser.getAttributeValue(null, "href"));
							else
								currentNews.setLink(parser.nextText());
						} else if (name.equalsIgnoreCase(PUB_DATE) || name.equalsIgnoreCase(PUBLISHED))
							currentNews.setPubDate(parser.nextText());
						else if (name.equalsIgnoreCase(TITLE))
							currentNews.setTitle(parser.nextText());
						else if (name.equalsIgnoreCase(DESCRIPTION) || name.equalsIgnoreCase(CONTENT))
							currentNews.setDesc(parser.nextText().replaceAll("<(.|\n)*[^>]?>",""));
						else if (name.equalsIgnoreCase(ENCOL)) {
							if (parser.getAttributeCount() > 0)
								currentNews.setImageURL(parser.getAttributeValue(null, "url"));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(ITEM) || name.equalsIgnoreCase(ENTRY) && currentNews != null) {
						try {
							news.add(currentNews);
						} catch (Exception e) {
							Log.w(LOG_TAG, "Something is gone wrong on add data to the List class");
						}
					}
					break;
			}
			eventType = parser.next();
		}
		return news;
	}
}