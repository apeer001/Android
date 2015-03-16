/*
 * Copyright (C) 2015 Jonathan Steele
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

package com.inoles.nolesfootball;

import android.app.Fragment;

import java.util.List;

import rx.Observable;
import rx.Observable.Transformer;
import rx.android.app.AppObservable;

class RxUtils {
    private RxUtils() {}

    /*public static <T> Transformer<?, ?> applyActivitySchedulers(final Activity activity) {
        return new Transformer<?, ?>() {
            @Override
            public Observable<List<T>> call(Observable<List<T>> listObservable) {
                return AppObservable.bindActivity(activity, listObservable);
            }
        };
    }*/

    public static <T> Transformer<List<T>, List<T>> applyFragmentSchedulers(final Fragment fragment) {
        return new Transformer<List<T>, List<T>>() {
            @Override
            public Observable<List<T>> call(Observable<List<T>> listObservable) {
                return AppObservable.bindFragment(fragment, listObservable);
            }
        };
    }
}
