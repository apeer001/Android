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

import android.util.Log;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

final class BindsAdapter<T> implements Observable.Operator<List<T>, List<T>> {
    private static final String LOG_TAG = BindsAdapter.class.getName();

    private final AbstractBaseAdapter<T> mAdapter;

    BindsAdapter(AbstractBaseAdapter<T> adapter) {
        mAdapter = adapter;
    }

    @Override
    public Subscriber<? super List<T>> call(Subscriber<? super List<T>> subscriber) {
        return new Subscriber<List<T>>() {
            @Override
            public void onCompleted() {
               mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onNext(List<T> list) {
                mAdapter.add(list);
            }
        };
    }
}
