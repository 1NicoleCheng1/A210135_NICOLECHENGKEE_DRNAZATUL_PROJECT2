package com.example.a210135_nicolechengkee_drnazatul_project2.network

import com.example.a210135_nicolechengkee_drnazatul_project2.data.RemotiveResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RemotiveApi {
    @GET("api/remote-jobs")
    suspend fun getJobs(
        @Query("category") category: String = "software-dev",
        @Query("limit")    limit: Int    = 20
    ): RemotiveResponse

    @GET("api/remote-jobs")
    suspend fun searchJobs(
        @Query("search") search: String,
        @Query("limit")  limit: Int = 20
    ): RemotiveResponse
}

object RetrofitInstance {
    val api: RemotiveApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://remotive.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemotiveApi::class.java)
    }
}
