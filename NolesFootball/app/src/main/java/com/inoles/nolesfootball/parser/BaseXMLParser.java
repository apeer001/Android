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

package com.inoles.nolesfootball.parser;

import android.util.Xml;

import com.inoles.nolesfootball.HttpUtils;
import com.squareup.okhttp.Response;

import org.xml.sax.ContentHandler;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

abstract class BaseXMLParser<T> {
    private final String mURL;

    BaseXMLParser(String url) {
        mURL = url;
    }

    public Observable<List<T>> pullDataFromNetwork() {
        return HttpUtils.getDefaultInstance()
                .getResponse(mURL)
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return HttpUtils.mapResponseToString(response);
                    }
                }).flatMap(new Func1<String, Observable<T>>() {
                    @Override
                    public Observable<T> call(final String data) {
                        return Observable.create(new Observable.OnSubscribe<T>() {
                            @Override
                            public void call(Subscriber<? super T> subscriber) {
                                try {
                                    Xml.parse(data, getContentHandler(subscriber));
                                    subscriber.onCompleted();
                                } catch (Throwable throwable) {
                                    subscriber.onError(throwable);
                                }
                            }
                        });
                    }
                }).subscribeOn(Schedulers.io()).toList();
    }

    abstract ContentHandler getContentHandler(Subscriber<? super T> subscriber);
}
