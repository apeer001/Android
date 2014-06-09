/*
 * Copyright (c) 2013 Jonathan Steele
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.inoles.collegesports;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

class StringReaderRequest extends Request<StringReader> {
    private final Response.Listener<StringReader> listener;

    /**
     * Creates a new request with the given method (one of the values from URL and error listener.
     * Note that the normal response listener is not provided here as delivery of responses is
     * provided by subclasses, who have a better idea of how to deliver an already-parsed response.
     *
     * @param url URL of the request to make
     * @param listener Listener to receive the StringReader response
     */
    public StringReaderRequest(String url, Response.Listener<StringReader> listener,
                      Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed
     * response to their listeners. The given response is guaranteed to
     * be non-null; responses that fail to parse are not delivered.
     *
     * @param response The parsed response returned by
     *                 {@link #parseNetworkResponse(NetworkResponse)}
     */
    @Override
    protected void deliverResponse(StringReader response) {
        listener.onResponse(response);
    }

    /**
     * Subclasses must implement this to parse the raw network response
     * and return an appropriate response type. This method will be
     * called from a worker thread. The response will not be delivered
     * if you return null.
     *
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    @Override
    protected Response<StringReader> parseNetworkResponse(NetworkResponse response) {
        try {
            String xml = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            StringReader sr = new StringReader(xml);
            return Response.success(sr, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
