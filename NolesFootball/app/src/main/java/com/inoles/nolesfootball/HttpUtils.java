package com.inoles.nolesfootball;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.Reader;

import rx.Observable;
import rx.Subscriber;

public class HttpUtils {
    private static HttpUtils sInstance;

    private final OkHttpClient mClient = new OkHttpClient();

    public static HttpUtils getInstance() {
        if (sInstance == null) {
            sInstance = new HttpUtils();
        }

        return sInstance;
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

    public static Observable<Reader> mapResponseReader(final Response response) {
        return Observable.create(new Observable.OnSubscribe<Reader>() {
            @Override
            public void call(Subscriber<? super Reader> subscriber) {
                Reader reader = response.body().charStream();
                try {
                    subscriber.onNext(reader);
                    subscriber.onCompleted();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                } finally {
                    try {
                        reader.close();
                    } catch (Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                }
            }
        });
    }
}
