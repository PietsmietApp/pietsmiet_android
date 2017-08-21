package de.pscom.pietsmiet.repository;

import org.mockito.Mock;

import de.pscom.pietsmiet.view.MainActivity;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

abstract class MainTestPresenter {
    @Mock
    protected MainActivity mMockContext;

    public abstract <T extends MainRepository> T preparePresenter() throws Exception;


    protected Retrofit getRetrofit(MockWebServer mockWebServer) {
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.create();
        return new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();
    }
}
