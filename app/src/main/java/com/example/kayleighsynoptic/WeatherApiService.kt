package com.example.kayleighsynoptic

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherApiService {
    @GET("api/v1/weather/{city}")
    suspend fun getWeather(@Path("city") city: String): Response<WeatherResponse>
}

