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

package com.itnoles.shared.util.base;

import android.text.format.DateUtils;

import java.io.IOException;
import java.io.InputStream;

public abstract class HttpTransport {
    protected static final int CONN_TIMEOUT = 20 * (int) DateUtils.SECOND_IN_MILLIS;

    public abstract LowLevelHttpResponse buildResponse(String url) throws IOException;

    /**
     * Default implementation does nothing, but subclasses may override to attempt to abort the
     * connection or release allocated system resources for this connection.
     */
    public void shutdown() {
    }

    public abstract class LowLevelHttpResponse {
        public abstract InputStream execute() throws IOException;
        public abstract void disconnect();
    }
}