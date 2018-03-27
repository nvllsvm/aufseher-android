package net.nullsum.aufseher

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT

class LightsAPI(baseUrl: String) {
    var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    interface LightsService {

        @Headers("Authorization: thisisnotsecure")
        @PUT("/apartment/lights")
        fun setColorMode(@Body colorMode: ColorMode): Call<ColorMode>
    }
}
