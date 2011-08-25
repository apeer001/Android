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

package com.itnoles.shared.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.Log;

import com.itnoles.shared.io.RemoteExecutor;
import com.itnoles.shared.io.WorksheetsHandler;
import com.itnoles.shared.R;
import com.itnoles.shared.receiver.ConnectivityChangedReceiver;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class SyncService extends IntentService
{
    private static final String TAG = "SyncService";

    private static final int CONN_TIMEOUT = 20 * (int) DateUtils.SECOND_IN_MILLIS;
    private static final int SOCKET_BUFFER_SIZE = 8192;

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private RemoteExecutor mRemoteExecutor;
    private ConnectivityManager mConnectManager;

    public SyncService()
    {
       super(TAG);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        final DefaultHttpClient httpClient = getHttpClient(this);
        mRemoteExecutor = new RemoteExecutor(httpClient, getContentResolver());
        mConnectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "onHandleIntent(intent=" + intent.toString() + ")");

        // Check to see if we are connected to a data network.
        final NetworkInfo activeNetwork = mConnectManager.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            // Enable the Connectivity Changed Receiver to listen for connection to a network
            final PackageManager pm = getPackageManager();
            final ComponentName connectivityReceiver = new ComponentName(this, ConnectivityChangedReceiver.class);
            pm.setComponentEnabledSetting(connectivityReceiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            return;
        }

        final long startRemote = System.currentTimeMillis();
        mRemoteExecutor.executeWithPullParser(getResources().getString(R.string.worksheet_url), new WorksheetsHandler(mRemoteExecutor));
        Log.d(TAG, "remote sync took " + (System.currentTimeMillis() - startRemote) + "ms");
    }

    /**
     * Generate and return a {@link HttpClient} configured for general use,
     * including setting an application-specific user-agent string.
     */
    public static DefaultHttpClient getHttpClient(Context context)
    {
        final HttpParams params = new BasicHttpParams();

        // Use generous timeouts for slow mobile networks
        HttpConnectionParams.setConnectionTimeout(params, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONN_TIMEOUT);

        HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);
        HttpProtocolParams.setUserAgent(params, buildUserAgent(context));

        final DefaultHttpClient client = new DefaultHttpClient(params);
        client.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context)
            {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor() {
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

        return client;
    }

    /**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
    private static String buildUserAgent(Context context)
    {
        try {
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