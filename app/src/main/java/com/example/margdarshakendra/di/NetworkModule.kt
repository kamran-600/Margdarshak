package com.example.margdarshakendra.di

import com.example.margdarshakendra.api.AddressSearchApi
import com.example.margdarshakendra.api.TokenInterceptor
import com.example.margdarshakendra.api.UserApi
import com.example.margdarshakendra.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }


    @Singleton
    @Provides
    fun providesOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(tokenInterceptor).build()
    }


    @Singleton
    @Provides
    fun providesAddressSearchApi(
        okHttpClient: OkHttpClient,
        retrofitBuilder: Retrofit.Builder
    ): AddressSearchApi {
        return retrofitBuilder.client(okHttpClient).build().create(AddressSearchApi::class.java)
    }


    @Singleton
    @Provides
    fun providesUserApi(retrofitBuilder: Retrofit.Builder): UserApi {
        return retrofitBuilder.build().create(UserApi::class.java)
    }


}