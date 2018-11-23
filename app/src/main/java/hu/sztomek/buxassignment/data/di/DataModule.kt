package hu.sztomek.buxassignment.data.di

import android.app.Application
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.BackoffStrategy
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.scarlet.websocket.okhttp.request.RequestFactory
import dagger.Module
import dagger.Provides
import hu.sztomek.buxassignment.data.DataRepositoryImpl
import hu.sztomek.buxassignment.data.api.RestApi
import hu.sztomek.buxassignment.data.api.WebSocketApi
import hu.sztomek.buxassignment.data.error.ErrorHandlingCallAdapterFactory
import hu.sztomek.buxassignment.domain.data.DataRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Named("restUrl")
    fun provideRestApiBaseUrl() = "https://api.beta.getbux.com"

    @Provides
    @Named("wsUrl")
    fun provideWebSocketApiUrl() = "https://rtf.beta.getbux.com/subscriptions/me"

    @Provides
    @Named("authToken")
    fun provideAuthToken() = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWExMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInNjcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiIsImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg"

    @Singleton
    @Provides
    fun provideHttpLoggerInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Timber.d(message) }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @Singleton
    @Provides
    @Named("headerInterceptor")
    fun provideHeaderInterceptor(@Named("authToken") authToken: String): Interceptor {
        return Interceptor {
            it.proceed(it.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "nl-NL,en;q=0.8")
                .addHeader("Authorization", authToken)
                .build()
            )
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor,
                            @Named("headerInterceptor") headerInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(@Named("restUrl") url: String,
                        okHttpClient: OkHttpClient,
                        callAdapterFactory: ErrorHandlingCallAdapterFactory): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(callAdapterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideRestApi(retrofit: Retrofit): RestApi {
        return retrofit.create(RestApi::class.java)
    }

    @Singleton
    @Provides
    fun provideWebSocketFactory(@Named("wsUrl") url: String,
                                @Named("authToken") authToken: String,
                                okHttpClient: OkHttpClient): WebSocket.Factory {
        return okHttpClient.newWebSocketFactory(object: RequestFactory {
            override fun createRequest() =
                Request.Builder().url(url)
                    .addHeader("Accept-Language", "nl-NL,en;q=0.8")
                    .addHeader("Authorization", authToken)
                    .build()
        })
    }

    @Singleton
    @Provides
    fun provideBackoffStrategy(): BackoffStrategy {
        return ExponentialWithJitterBackoffStrategy(30 * 1_000L, 2 * 60 * 1_000L)
    }

    @Singleton
    @Provides
    fun provideLifecycle(application: Application): Lifecycle {
        return AndroidLifecycle.ofApplicationForeground(application)
    }

    @Singleton
    @Provides
    fun provideScarlet(webSocketFactory: WebSocket.Factory,
                       backoffStrategy: BackoffStrategy,
                       lifecycle: Lifecycle): Scarlet {
        return Scarlet.Builder()
            .webSocketFactory(webSocketFactory)
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
            .backoffStrategy(backoffStrategy)
            .lifecycle(lifecycle)
            .build()
    }

    @Singleton
    @Provides
    fun provideWsApi(scarlet: Scarlet): WebSocketApi {
        return scarlet.create()
    }

    @Singleton
    @Provides
    fun provideRepository(restApi: RestApi, webSocketApi: WebSocketApi): DataRepository {
        return DataRepositoryImpl(restApi, webSocketApi)
    }

}