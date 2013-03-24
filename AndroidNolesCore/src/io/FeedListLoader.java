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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.itnoles.shared.io;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * A custom Loader that loads all of the XML content.
 */
abstract class FeedListLoader<T> extends AsyncTaskLoader<List<T>> {
    private List<T> mFeed;

    protected final String mURL;

    FeedListLoader(Context context, String url) {
        super(context);
        mURL = url;
    }

    /**
     * Called when there is new data to deliver to the client. The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<T> feed) {
        mFeed = feed;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(feed);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mFeed != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mFeed);
        } else {
            // start a load when mFeed is null
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'feed'
        // if needed.
        if (mFeed != null) {
            mFeed.clear();
        }
    }
}