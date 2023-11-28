package com.example.margdarshakendra.api

import android.content.Context
import com.example.margdarshakendra.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor() : Interceptor {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        val token = tokenManager.getToken()

        request.addHeader("Authorization", "Bearer $token")

        return chain.proceed(request.build())
    }

}