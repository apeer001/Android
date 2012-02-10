/*
 * Copyright 2011 Google Inc.
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

package com.itnoles.shared.util;

import android.content.Context;

import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.base.ISharedPreferenceSaver;
import com.itnoles.shared.util.base.IStrictMode;

/**
 * Factory class to create the correct instances
 * of a variety of classes with platform specific
 * implementations.
 */
public final class PlatformSpecificImplementationFactory {
    private PlatformSpecificImplementationFactory() {}

    /**
     * Create a new IStrictMode instance.
     * @return IStrictMode or null if it is not Gingerbread
     */
    public static IStrictMode getStrictMode() {
        return SportsConstants.SUPPORTS_GINGERBREAD ? new GingerbreadStrictMode() : null;
    }

    /**
     * Create a new SharedPreferenceSaver instances
     * @param context Context
     * @return SharedPreferenceSaver
     */
    public static ISharedPreferenceSaver getSharedPreferenceSaver(Context ctx) {
        return SportsConstants.SUPPORTS_GINGERBREAD ? new GingerbreadSharedPreferenceSaver(ctx) : new FroyoSharedPreferenceSaver(ctx);
    }
}