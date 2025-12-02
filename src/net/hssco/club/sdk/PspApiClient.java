package net.hssco.club.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import net.hssco.club.sdk.api.PspApiService;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

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
        return create(baseUrl, enableLogging, false);
    }

    public static PspApiClient create(String baseUrl, boolean enableLogging, boolean trustAllCertificates) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(enableLogging ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                // Some PSP gateways close the socket immediately after sending the response,
                // which can manifest as "unexpected end of stream" on keep-alive connections.
                // Force HTTP/1.1 with a short-lived connection pool and a "Connection: close"
                // hint to avoid reusing the socket and complete the call successfully.
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(0, 1, TimeUnit.SECONDS))
                .addNetworkInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("Connection", "close")
                                .build()));

        if (trustAllCertificates) {
            TrustManager[] trustAllCerts = createTrustAllManagers();
            SSLSocketFactory sslSocketFactory = createTrustAllSocketFactory(trustAllCerts);
            okHttpClientBuilder
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(createTrustAllHostnameVerifier());
        }

        OkHttpClient okHttpClient = okHttpClientBuilder.build();

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

    private static TrustManager[] createTrustAllManagers() {
        return new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                // Intentionally trusting all client certificates for development endpoints.
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                // Intentionally trusting all server certificates for development endpoints.
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } };
    }

    private static SSLSocketFactory createTrustAllSocketFactory(TrustManager[] trustAllCerts) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create trust-all SSL socket factory", e);
        }
    }

    private static HostnameVerifier createTrustAllHostnameVerifier() {
        return (hostname, session) -> true;
    }
}
