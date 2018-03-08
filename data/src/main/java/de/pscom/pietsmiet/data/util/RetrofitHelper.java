package de.pscom.pietsmiet.data.util;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public abstract class RetrofitHelper {
    private static OkHttpClient client;

    public static Retrofit getRetrofit(String url) {
        return createRetrofit(url, GsonConverterFactory.create());
    }

    public static Retrofit getRetrofit(String url, Converter.Factory converterFactory) {
        return createRetrofit(url, converterFactory);
    }

    private static Retrofit createRetrofit(String url, Converter.Factory factory) {
        if (client == null) {
            //todo client = StethoHelper.configureInterceptor(new OkHttpClient.Builder()).build();
            client = new OkHttpClient.Builder().build();
        }
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(factory)
                .client(client)
                .addCallAdapterFactory(rxAdapter)
                .build();
    }
}
