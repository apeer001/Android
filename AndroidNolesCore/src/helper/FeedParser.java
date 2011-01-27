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
package com.itnoles.shared.helper;

import org.xml.sax.*; // XMLReader and InputSource

import android.util.Log;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import com.itnoles.shared.*; // News and Utilities

/**
 * FeedParser
 * class that parse Atom and RSS feeds
 * @author Jonathan Steele
 */

public class FeedParser
{
	private static final String TAG = "FeedParser";

	public static List<News> parse(String urlString)
	{
		FeedHandler handler = new FeedHandler();
		InputStream inputStream = null;
		try {
			// Get a SAXParser from the SAXPArserFactory.
			SAXParserFactory factory = SAXParserFactory.newInstance();
			
			// Get XMLParser from the SAXParser
			XMLReader xr = factory.newSAXParser().getXMLReader();
			xr.setContentHandler(handler);
			
			inputStream = Utilities.openStream(urlString);
			// Parse the xml-data from InputStream.
			xr.parse(new InputSource(inputStream));
		} catch (Exception e) {
			Log.e(TAG, "bad feed parsing", e);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				Log.e(TAG, "can't close inputstream", e);
			}
		}
		// Parsing has finished.
		// Our handler now provides the parsed data to us.
		return handler.getMessages();
	}
}