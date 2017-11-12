package com.example.draje.colortest

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

class LightsAPI(baseUrl: String) {
    var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

    }

    interface LightsService {
        @get:GET("/")
        val colorMode: Call<ColorMode>

        @PUT("/")
        fun setColorMode(@Body colorMode: ColorMode): Call<ColorMode>
    }
}