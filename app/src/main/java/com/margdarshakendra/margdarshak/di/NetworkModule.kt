package com.margdarshakendra.margdarshak.di

import com.margdarshakendra.margdarshak.api.AddressSearchApi
import com.margdarshakendra.margdarshak.api.TokenInterceptor
import com.margdarshakendra.margdarshak.api.DashboardApi
import com.margdarshakendra.margdarshak.api.UserApi
import com.margdarshakendra.margdarshak.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Builder {
        return Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }


    @Singleton
    @Provides
    fun providesOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(tokenInterceptor).addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build()
    }

    //.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

    @Singleton
    @Provides
    fun providesAddressSearchApi(
        okHttpClient: OkHttpClient,
        retrofitBuilder: Builder
    ): AddressSearchApi {
        return retrofitBuilder.client(okHttpClient).build().create(AddressSearchApi::class.java)
    }


    @Singleton
    @Provides
    fun providesUserApi(retrofitBuilder: Builder): UserApi {
        return retrofitBuilder.build().create(UserApi::class.java)
    }


    @Singleton
    @Provides
    fun providesDashboardApi(okHttpClient: OkHttpClient, retrofitBuilder: Builder) : DashboardApi{
        return retrofitBuilder.client(okHttpClient).build().create(DashboardApi::class.java)
    }


}