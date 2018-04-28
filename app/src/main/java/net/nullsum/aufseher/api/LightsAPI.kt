package net.nullsum.aufseher.api

import net.nullsum.aufseher.ColorMode
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.PUT

class LightsAPI(baseUrl: String) {
    val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient().newBuilder().addInterceptor(LightsInterceptor()).build())
            .build()

    interface LightsService {

        @PUT("/apartment/lights")
        fun setColorMode(@Body colorMode: ColorMode): Call<ColorMode>
    }
}
