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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.itnoles.shared.util.base.HttpTransport;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public final class ApacheHttp extends HttpTransport
{
    private static final String LOG_TAG = "ApacheHttp";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private final HttpClient httpClient;
    private String mUrl;
    
    public ApacheHttp(Context context, String url)
    {
        super(context);
        this.mUrl = url;

        final HttpParams params = new BasicHttpParams();
        // Use generous timeouts for slow mobile networks
        HttpConnectionParams.setConnectionTimeout(params, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONN_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpProtocolParams.setUserAgent(params, buildUserAgent());
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        // See http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html
        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        final ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, registry);
        httpClient = new DefaultHttpClient(connectionManager, params);
        enableGZIPToHttpClient();
    }
    
    private void enableGZIPToHttpClient()
    {
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context)
            {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });
        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context)
            {
                // Inflate any responses compressed with gzip
                final HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });
    }
    
    @Override
    public InputStream execute() throws IOException
    {
        final HttpGet request = new HttpGet(mUrl);
        final HttpResponse resp = httpClient.execute(request);
        final int status = resp.getStatusLine().getStatusCode();
        if (status != HttpStatus.SC_OK) {
            Log.w(LOG_TAG, "Unexpected server response " + resp.getStatusLine() + " for " + request.getRequestLine());
        }
        final HttpEntity entity = resp.getEntity();
        return entity == null ? null : entity.getContent();
    }

    @Override
    public void shutdown()
    {
        httpClient.getConnectionManager().shutdown();
    }
    
    /**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
    private String buildUserAgent()
    {
        try {
            final Context context = getContext();
            final PackageManager manager = context.getPackageManager();
            final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            // Some APIs require "(gzip)" in the user-agent string.
            return info.packageName + "/" + info.versionName + " (" + info.versionCode + ") (gzip)";
        }
        catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Simple {@link HttpEntityWrapper} that inflates the wrapped
     * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
     */
    private static class InflatingEntity extends HttpEntityWrapper
    {
        public InflatingEntity(HttpEntity wrapped)
        {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException
        {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength()
        {
            return -1;
        }
    }
}