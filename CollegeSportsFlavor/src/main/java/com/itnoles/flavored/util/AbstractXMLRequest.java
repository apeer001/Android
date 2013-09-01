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

package com.itnoles.flavored.util;

import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public abstract class AbstractXMLRequest<T> extends Request<T> {
    private static final String LOG_TAG = "AbstractXMLRequest";

    private final Response.Listener<T> mListener;

    private static XmlPullParserFactory sXmlPullParserFactory;
    static {
        try {
            sXmlPullParserFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "Could not instantiate XmlPullParserFactory", e);
        }
    }
    public AbstractXMLRequest(String url, Response.Listener<T> listener) {
        super(Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "xml data failed to load", error);
            }
        });
        mListener = listener;
    }


    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String xmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            XmlPullParser parser = sXmlPullParserFactory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            return Response.success(onPostNetworkResponse(parser), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (XmlPullParserException xmle) {
            return Response.error(new ParseError(xmle));
        } catch (IOException ioe) {
            return Response.error(new ParseError(ioe));
        }
    }

    public abstract T onPostNetworkResponse(XmlPullParser parser) throws XmlPullParserException, IOException;
}