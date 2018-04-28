package net.nullsum.aufseher.api

import okhttp3.Interceptor
import okhttp3.Response

class LightsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "thisisnotsecure")
                .build()
        return chain.proceed(authorizedRequest)
    }
}