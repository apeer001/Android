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

import android.content.Context;

import com.itnoles.shared.SportsConstants;
import com.itnoles.shared.util.base.IStrictMode;
import com.itnoles.shared.util.base.SharedPreferenceSaver;

/**
 * Factory class to create the correct instances
 * of a variety of classes with platform specific
 * implementations.
 */
public class PlatformSpecificImplementationFactory
{
    /**
     * Create a new StrictMode instance.
     * @return StrictMode
     */
    public static IStrictMode getStrictMode()
    {
        if (SportsConstants.SUPPORTS_GINGERBREAD) {
            return new GingerbreadStrictMode();
        }
        return null;
    }

    /**
     * Create a new SharedPreferenceSaver
     * @param context Context
     * @return SharedPreferenceSaver
     */
    public static SharedPreferenceSaver getSharedPreferenceSaver(Context ctx)
    {
        if (SportsConstants.SUPPORTS_GINGERBREAD) {
            return new GingerbreadSharedPreferenceSaver(ctx);
        }
        else if (SportsConstants.SUPPORTS_FROYO) {
            return new FroyoSharedPreferenceSaver(ctx);
        }
        return new LegacySharedPreferenceSaver(ctx);
    }
}