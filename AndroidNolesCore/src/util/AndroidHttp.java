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

package com.itnoles.shared.util;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.itnoles.shared.util.base.HttpTransport;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;

final class AndroidHttp extends HttpTransport {
    private static final String LOG_TAG = "AndroidHttp";
    private final AndroidHttpClient mHttpClient;

    public AndroidHttp() {
        this.mHttpClient = AndroidHttpClient.newInstance(null);
    }

    @Override
    public Response buildResponse(String url) {
        final HttpGet response = new HttpGet(url);
        mHttpClient.modifyRequestToAcceptGzipResponse(response);
        return new Response(response);
    }

    @Override
    public void shutdown() {
        mHttpClient.close();
    }

    final class Response extends LowLevelHttpResponse {
        private final HttpGet mRequest;

        public Response(HttpGet request) {
            this.mRequest = request;
        }

        @Override
        public InputStream execute() throws IOException {
            final HttpResponse resp = mHttpClient.execute(mRequest);
            final int status = resp.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                Log.w(LOG_TAG, "Unexpected server response " + resp.getStatusLine() + " for " + mRequest.getRequestLine());
                return null;
            }
            final HttpEntity entity = resp.getEntity();
            return mHttpClient.getUngzippedContent(entity);
        }

        @Override
        public void disconnect() {
            mRequest.abort();
        }
    }
}