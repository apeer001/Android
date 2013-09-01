package com.itnoles.flavored;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Because every project needs a Utils class.
 */
public class Utils {
    private static final OkHttpClient mHttpClient = new OkHttpClient();

    private Utils() {}

    public static InputStreamReader openUrlConnection(String url) throws IOException {
        HttpURLConnection connection = mHttpClient.open(new URL(url));
        return new InputStreamReader(connection.getInputStream());
    }

    public static void ignoreQuietly(InputStreamReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ignored) {}
    }
}