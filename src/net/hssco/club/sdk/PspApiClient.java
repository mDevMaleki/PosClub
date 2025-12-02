package net.hssco.club.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import net.hssco.club.sdk.api.PspApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Factory for creating a Retrofit-backed {@link PspApiService} instance.
 */
public final class PspApiClient {

    /**
     * Replace with your production base URL (must end with a trailing slash).
     */
    public static final String DEFAULT_BASE_URL = "https://api.example.com/";

    private final Retrofit retrofit;
    private final PspApiService apiService;

    private PspApiClient(Retrofit retrofit) {
        this.retrofit = retrofit;
        this.apiService = retrofit.create(PspApiService.class);
    }

    public static PspApiClient createDefault() {
        return create(DEFAULT_BASE_URL, true);
    }

    public static PspApiClient create(String baseUrl) {
        return create(baseUrl, true);
    }

    public static PspApiClient create(String baseUrl, boolean enableLogging) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(enableLogging ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return new PspApiClient(retrofit);
    }

    public PspApiService getApiService() {
        return apiService;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
