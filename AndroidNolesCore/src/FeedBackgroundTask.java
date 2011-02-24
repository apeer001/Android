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
import org.apache.http.client.methods.HttpGet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.AsyncTask;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import android.util.Xml;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FeedBackgroundTask extends AsyncTask<String, News, Void> {
	private FeedAsyncTaskCompleteListener callback;
	private static final String LOG_TAG = "FeedBackgroundTask";
	
	// Constructor
	public FeedBackgroundTask(FeedAsyncTaskCompleteListener callback) {
		this.callback = callback;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		// Parse RSS or Atom Feed
		FeedHandler handler = new FeedHandler();
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(params[0]);
		try {
			HttpResponse response = client.execute(getRequest);
			final HttpEntity entity = response.getEntity();
			InputStream inputStream = null;
			try {
				inputStream = new BufferedInputStream(entity.getContent());
				// Parse the xml-data from InputStream.
				Xml.parse(inputStream, Xml.Encoding.UTF_8, handler);
			} finally {
				if (inputStream != null)
					inputStream.close();
				entity.consumeContent();
			}
		} catch (Exception e) {
			getRequest.abort();
			Log.e(LOG_TAG, "bad feed parsing", e);
		} finally {
			if (client != null)
				client.close();
		}
		// Parsing has finished.
		// Our handler now provides the parsed data to us.
		for (News news : handler.getMessages())
			publishProgress(news);
		return null;
	}
	
	@Override
	public void onProgressUpdate(News... values) {
		callback.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Void result) {
		callback.onTaskComplete(result);
	}
	
	private class FeedHandler extends DefaultHandler {
		private static final String TAG = "RssHandler";
		
		// names of the XML tags
		private static final String PUB_DATE = "pubDate";
		private static final String LINK = "link";
		private static final String TITLE = "title";
		private static final String ITEM = "item";
		private static final String ENTRY = "entry";
		private static final String PUBLISHED = "published";
		
		// Common Atom Format
		protected final SimpleDateFormat ISO8601_DATE_FORMATS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		private List<News> messages = new ArrayList<News>();
		private News currentMessage;
		private StringBuilder builder;
		private String mHrefAttribute; // href attribute from link element in Atom format

		public List<News> getMessages()
		{
			return messages;
		}
		
		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
		{
			super.startElement(uri, localName, name, attributes);
			builder = new StringBuilder();
			if (localName.equalsIgnoreCase(ITEM) || localName.equalsIgnoreCase(ENTRY))
				currentMessage = new News();
			else if (localName.equalsIgnoreCase("enclosure")) {
				if (attributes != null)
					currentMessage.setImageURL(attributes.getValue("url"));
			} else if (localName.equalsIgnoreCase("link")) {
				// Get href attribute from link element for Atom format
				if (attributes != null)
					mHrefAttribute = attributes.getValue("href");
			}
		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException
		{
			super.endElement(uri, localName, name);
			if (currentMessage != null)
			{
				if (localName.equalsIgnoreCase(TITLE))
					currentMessage.setTitle(builder.toString().trim());
				else if (localName.equalsIgnoreCase(LINK)) {
					if (mHrefAttribute != null)
						currentMessage.setLink(mHrefAttribute);
					else
						currentMessage.setLink(builder.toString().trim());
				}
				else if (localName.equalsIgnoreCase(PUB_DATE))
					currentMessage.setPubDate(builder.toString().trim());
				else if (localName.equalsIgnoreCase(PUBLISHED))
					setDate(ISO8601_DATE_FORMATS);
				else if (localName.equalsIgnoreCase(ITEM) || localName.equalsIgnoreCase(ENTRY))
					messages.add(currentMessage);
				
				// Reset the String Builder to Zero
				builder.setLength(0);
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}
		
		private void setDate(SimpleDateFormat sdf)
		{
			try {
				currentMessage.setPubDate(sdf.parse(builder.toString().trim()).toString());
			} catch (ParseException e) {
				Log.e(TAG, "bad date format", e);
			}
		}
	}
}