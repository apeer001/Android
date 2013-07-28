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

import android.content.Context;

import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * A Helper Class is for providing references to initalize RequestQueue and ImageLoader
 */
public class VolleyHelper {
    //private static final int IMAGECACHE_SIZE = 1024*1024*10;

    private static RequestQueue mRequestQueue;
    //private static ImageLoader mImageLoader;

    private VolleyHelper() {}

    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        //mImageLoader = new ImageLoader(mRequestQueue, new BitmapLrcCache(IMAGECACHE_SIZE));
    }

    public static RequestQueue getResultQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        }
        throw new IllegalStateException("RequestQueue not initialized. Did you forget to call init?");
    }

    /*public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        }
        throw new IllegalStateException("ImageLoader not initialized. Did you forget to call init");
    }*/
}