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

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public final class PackageManagerWrapper {
    private final Context mContext;

    public PackageManagerWrapper(Context context) {
    	this.mContext = context;
    }

    /**
     * Set the default setting for a package component (activity, receiver, service, provider).
     * @param compontentClass The default component for class.
     */
    public void setDefaultComponentSetting(Class compontentClass) {
        final PackageManager pm = mContext.getPackageManager();
        final ComponentName packageComponent = new ComponentName(mContext, compontentClass);
        pm.setComponentEnabledSetting(packageComponent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    /**
     * Set the enabled setting for a package component (activity, receiver, service, provider).
     * @param compontentClass The component to enable.
     */
    public void setComponentEnabledSetting(Class compontentClass) {
    	final PackageManager pm = mContext.getPackageManager();
    	final ComponentName packageComponent = new ComponentName(mContext, compontentClass);
        pm.setComponentEnabledSetting(packageComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * @return Retrieve packageInfo for current package Name
     */
    public PackageInfo getPackageInfo() {
    	final PackageManager pm = mContext.getPackageManager();
    	try {
    	    return pm.getPackageInfo(mContext.getPackageName(), 0);
    	} catch (PackageManager.NameNotFoundException e) {
    		return null;
    	}
    }
}