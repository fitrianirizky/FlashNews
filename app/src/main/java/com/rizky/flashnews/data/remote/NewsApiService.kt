package com.rizky.flashnews.data.remote

import com.rizky.flashnews.domain.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines")
    suspend fun getBreakingNews(
        @Query("category") category: String,
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String = API_KEY
    ): NewsResponse

    @GET("everything")
    suspend fun searchForNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = API_KEY
    ): NewsResponse

    companion object{
        const val BASE_URL = "https://newsapi.org/v2/"
        const val API_KEY = "5747ea67f6c3499596b8281d4904209b"
    }
}