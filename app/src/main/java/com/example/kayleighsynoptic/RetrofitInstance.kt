package com.example.kayleighsynoptic

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitInstance.kt
object RetrofitInstance {
    private const val BASE_URL = "https://api.jamesdecelis.com/"


    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)



    }
}