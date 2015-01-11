package com.inoles.nolesfootball.parser;

import android.util.Xml;

import com.inoles.nolesfootball.HttpUtils;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
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
        return HttpUtils.getInstance()
                .getResponse(mURL)
                .flatMap(new Func1<Response, Observable<Reader>>() {
                    @Override
                    public Observable<Reader> call(Response response) {
                        return HttpUtils.mapResponseReader(response);
                    }
                }).flatMap(new Func1<Reader, Observable<List<T>>>() {
                    @Override
                    public Observable<List<T>> call(final Reader reader) {
                        return Observable.create(new Observable.OnSubscribe<List<T>>() {
                            @Override
                            public void call(Subscriber<? super List<T>> subscriber) {
                                try {
                                    XmlPullParser parser = Xml.newPullParser();
                                    parser.setInput(reader);
                                    subscriber.onNext(parseXML(parser));
                                    subscriber.onCompleted();
                                } catch (Throwable throwable) {
                                    subscriber.onError(throwable);
                                }
                            }
                        });
                    }
                }).subscribeOn(Schedulers.io());
    }

    abstract List<T> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException;
}
