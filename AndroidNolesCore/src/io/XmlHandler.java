/*
 * Copyright (C) 2011 Jonathan Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itnoles.shared.io;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.itnoles.shared.SportsConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public abstract class XmlHandler
{
    private static final String LOG_TAG = "XmlHandler";

    /**
     * Parse the given {@link XmlPullParser}, turning into a series of
     * {@link ContentProviderOperation} that are immediately applied using the
     * given {@link ContentResolver}.
     */
    public void parseAndApply(XmlPullParser parser, ContentResolver resolver)
    {
        try {
            final ArrayList<ContentProviderOperation> batch = parse(parser, resolver);
            resolver.applyBatch(SportsConstants.CONTENT_AUTHORITY, batch);
        }
        catch (XmlPullParserException e) {
            Log.w(LOG_TAG, "Problem parsing XML response", e);
        }
        catch (IOException e) {
            Log.w(LOG_TAG, "Problem reading response", e);
        }
        catch (RemoteException e) {
            // Failed binder transactions aren't recoverable
            Log.w(LOG_TAG, "Problem applying batch operation", e);
        }
        catch (OperationApplicationException e) {
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