package com.tuempresa.stockapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.tuempresa.stockapp.BuildConfig

object RetrofitClient {
    // Read base URL from BuildConfig so it's easy to change per build (emulator vs device)
    private val BASE_URL: String = BuildConfig.BASE_URL

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
