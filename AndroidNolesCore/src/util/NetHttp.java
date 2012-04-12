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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class NetHttp {
    // 8 KB Buffer Size for BufferedInputStream
    private static final int BUFFER_SIZE = 8192;

    private final HttpURLConnection mConnection;

    public NetHttp(String url) throws IOException {
        // Disable connection pooling for pre-Gingerbread
        if (!Utils.isGingerbread()) {
            System.setProperty("http.keepAlive", "false");
        }

        HttpURLConnection.setFollowRedirects(false);
        this.mConnection = (HttpURLConnection) new URL(url).openConnection();
    }

    /**
     * Get InputStream with buffered from HttpURLConnection.
     * @return new BufferedInputStream
     */
    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(mConnection.getInputStream(), BUFFER_SIZE);
    }

    public void close() {
        if (!Utils.isGingerbread()) {
            mConnection.disconnect();
        }
    }
}