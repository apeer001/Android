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

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

public class HttpUtils {
    private static final int CONNECT_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 30;
    private static final HttpUtils INSTANCE = new HttpUtils();

    private final OkHttpClient mClient = new OkHttpClient();

    private HttpUtils() {
        mClient.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        mClient.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        mClient.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
    }

    public static HttpUtils getDefaultInstance() {
        return INSTANCE;
    }

    public Observable<Response> getResponse(final String url) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    Request request = new Request.Builder().url(url).build();
                    subscriber.onNext(mClient.newCall(request).execute());
                    subscriber.onCompleted();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }
            }
        });
    }

    @NonNull
    public static Observable<String> mapResponseToString(final Response response) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(response.body().string());
                    subscriber.onCompleted();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }
            }
        });
    }
}
