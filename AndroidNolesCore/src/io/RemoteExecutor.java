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

package com.itnoles.shared.io;

import android.content.ContentResolver;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RemoteExecutor
{
    private static final String LOG_TAG = "RemoteExecutor";
    private static SAXParserFactory sSAXFactory;
    private static XmlPullParserFactory sPullFactory;

    private final DefaultHttpClient mHttpClient;
    private final ContentResolver mResolver;

    public RemoteExecutor(DefaultHttpClient httpClient, ContentResolver resolver)
    {
        mHttpClient = httpClient;
        mResolver = resolver;
    }

    private InputStream getInputStream(HttpGet request) throws IOException
    {
        final HttpResponse resp = mHttpClient.execute(request);
        final int status = resp.getStatusLine().getStatusCode();
        if (status != HttpStatus.SC_OK) {
            Log.w(LOG_TAG, "Unexpected server response " + resp.getStatusLine() + " for " + request.getRequestLine());
        }
        return resp.getEntity().getContent();
    }

    private static XmlPullParser parseWithXMLPullParser(InputStream in) throws XmlPullParserException
    {
        if (sPullFactory == null) {
            sPullFactory = XmlPullParserFactory.newInstance();
        }
        final XmlPullParser parser = sPullFactory.newPullParser();
        parser.setInput(in, null);
        return parser;
    }

    public void executeWithPullParser(String url, XmlHandler handler)
    {
        final HttpGet request = new HttpGet(url);
        try {
            final InputStream input = getInputStream(request);
            try {
                final XmlPullParser parser = parseWithXMLPullParser(input);
                handler.parseAndApply(parser, mResolver);
            }
            catch (XmlPullParserException e) {
                request.abort();
                Log.w(LOG_TAG, "Malformed response for " + request.getRequestLine(), e);
            }
            finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        catch (IOException e) {
            request.abort();
            Log.w(LOG_TAG, "Problem reading remote response for " + request.getRequestLine(), e);
        }
    }

    private static void parseWithSAXParser(InputStream in, DefaultHandler handler) throws SAXException, ParserConfigurationException, IOException
    {
        if (sSAXFactory == null) {
            sSAXFactory = SAXParserFactory.newInstance();
        }
        final SAXParser saxParser = sSAXFactory.newSAXParser();
        final XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(handler);
        reader.parse(new InputSource(in));
    }

    public void executeWithSAXParser(String url, DefaultHandler handler)
    {
        final HttpGet request = new HttpGet(url);
        try {
            final InputStream input = getInputStream(request);
            try {
                parseWithSAXParser(input, handler);
            }
            catch (SAXException e) {
                request.abort();
                Log.w(LOG_TAG, "Malformed response for " + request.getRequestLine(), e);
            }
            catch (ParserConfigurationException e) {
                request.abort();
                Log.w(LOG_TAG, "Serious Configuration Error", e);
            }
            finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        catch (IOException e) {
            request.abort();
            Log.w(LOG_TAG, "Problem reading remote response for " + request.getRequestLine(), e);
        }
    }
}