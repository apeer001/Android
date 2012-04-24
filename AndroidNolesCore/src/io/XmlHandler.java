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

package com.itnoles.shared.io;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public abstract class XmlHandler {
    private static final String LOG_TAG = "XmlHandler";
    static final String ENTRY = "entry";

    private final String mAuthority;

    public XmlHandler(String authority) {
        this.mAuthority = authority;
    }

    /**
     * Parse the given {@link XmlPullParser}, turning into a series of
     * {@link ContentProviderOperation} that are immediately applied using the
     * given {@link ContentResolver}.
     */
    public void parseAndApply(XmlPullParser parser, ContentResolver resolver) {
        try {
            final ArrayList<ContentProviderOperation> batch = parse(parser, resolver);
            resolver.applyBatch(mAuthority, batch);
        } catch (XmlPullParserException e) {
            Log.w(LOG_TAG, "Problem parsing XML response", e);
        } catch (IOException e) {
            Log.w(LOG_TAG, "Problem reading response", e);
        } catch (RemoteException e) {
            // Failed binder transactions aren't recoverable
            Log.w(LOG_TAG, "Problem applying batch operation", e);
        } catch (OperationApplicationException e) {
            // Failures like constraint violation aren't recoverable
            // wrapping around to retry parsing again.
            Log.w(LOG_TAG, "Problem applying batch operation", e);
        }
    }

    /**
     * Parse the given {@link XmlPullParser}, returning a set of
     * {@link ContentProviderOperation} that will bring the
     * {@link ContentProvider} into sync with the parsed data.
     */
    public abstract ArrayList<ContentProviderOperation> parse(XmlPullParser parser, ContentResolver resolver) throws XmlPullParserException, IOException;
}