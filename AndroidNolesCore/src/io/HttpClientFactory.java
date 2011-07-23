package com.itnoles.shared.io;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.DateUtils;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
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

public class HttpClientFactory
{
    private static final int CONN_TIMEOUT = 20 * (int) DateUtils.SECOND_IN_MILLIS;
    private static final int SOCKET_BUFFER_SIZE = 8192;

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    public static DefaultHttpClient createThreadSafeHttpClient(Context context)
    {
        final HttpParams params = new BasicHttpParams();

        // Use generous timeouts for slow mobile networks
        HttpConnectionParams.setConnectionTimeout(params, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONN_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);

        HttpProtocolParams.setUserAgent(params, buildUserAgent(context));
        
        // Create and initialize scheme registry
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        final ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        final DefaultHttpClient client = new DefaultHttpClient(cm, params);

        client.addRequestInterceptor(new HttpRequestInterceptor()
        {
            public void process(HttpRequest request, HttpContext context)
            {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor()
        {
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