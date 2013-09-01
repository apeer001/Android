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

import android.util.JsonReader;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

public abstract class AbstractJsonRequest<T> extends Request<T> {
    private static final String LOG_TAG = "AbstractJsonRequest";

    private final Response.Listener<T> mListener;

    public AbstractJsonRequest(String url, Response.Listener<T> listener) {
        super(Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "json data failed to load", error);
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
        JsonReader reader = null;
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            reader = new JsonReader(new StringReader(jsonString));
            return Response.success(onPostNetworkResponse(reader), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (IOException ioe) {
            return Response.error(new ParseError(ioe));
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException cioe) {
                Log.w(LOG_TAG, "Can't close JsonReader", cioe);
            }
        }
    }

    public abstract T onPostNetworkResponse(JsonReader reader) throws IOException;
}