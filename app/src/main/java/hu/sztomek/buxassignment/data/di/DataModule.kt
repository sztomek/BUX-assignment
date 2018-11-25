package hu.sztomek.buxassignment.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import hu.sztomek.buxassignment.data.DataRepositoryImpl
import hu.sztomek.buxassignment.data.api.RestApi
import hu.sztomek.buxassignment.data.api.WsApi
import hu.sztomek.buxassignment.data.api.WsApiImpl
import hu.sztomek.buxassignment.data.error.ErrorHandlingCallAdapterFactory
import hu.sztomek.buxassignment.data.json.WsMessageDeserializer
import hu.sztomek.buxassignment.data.model.ws.WebSocketMessage
import hu.sztomek.buxassignment.domain.data.DataRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Named("restUrl")
    fun provideRestApiBaseUrl() = "http://192.168.1.3:8080"

    @Provides
    @Named("wsUrl")
    fun provideWebSocketApiUrl() = "http://192.168.1.3:8080/subscriptions/me"

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
    fun provideGson(wsMessageDeserializer: WsMessageDeserializer): Gson {
        return GsonBuilder()
            .registerTypeAdapter(WebSocketMessage::class.java, wsMessageDeserializer)
            .create()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    fun provideRetrofit(@Named("restUrl") url: String,
                        okHttpClient: OkHttpClient,
                        converterFactory: Converter.Factory,
                        callAdapterFactory: ErrorHandlingCallAdapterFactory): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(url)
            .addConverterFactory(converterFactory)
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
    fun provideWsApi(okHttpClient: OkHttpClient, gson: Gson): WsApi {
        return WsApiImpl(okHttpClient, gson)
    }

    @Singleton
    @Provides
    fun provideRepository(restApi: RestApi, wsApi: WsApi): DataRepository {
        return DataRepositoryImpl(restApi, wsApi)
    }

}