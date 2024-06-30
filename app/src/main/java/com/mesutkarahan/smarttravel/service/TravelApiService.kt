package com.mesutkarahan.smarttravel.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface TravelApiService {
    @GET("travel-info")
    suspend fun getTravelInfo(): List<TravelInfoDto>

    companion object {
        private const val BASE_URL = "https://api.example.com/"

        fun create(): TravelApiService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(TravelApiService::class.java)
        }
    }
}