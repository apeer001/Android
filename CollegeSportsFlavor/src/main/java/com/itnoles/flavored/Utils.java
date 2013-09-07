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