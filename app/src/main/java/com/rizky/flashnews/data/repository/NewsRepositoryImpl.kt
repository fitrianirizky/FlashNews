package com.rizky.flashnews.data.repository

import android.util.Log
import com.rizky.flashnews.data.remote.NewsApiService
import com.rizky.flashnews.domain.model.Article
import com.rizky.flashnews.domain.model.repository.NewsRepository
import com.rizky.flashnews.util.Resource

class NewsRepositoryImpl(
    private val newsApi: NewsApiService
) : NewsRepository {

    override suspend fun getTopHeadlines(category: String): Resource<List<Article>> {
        return try {
            Log.d("NewsRepository", "Fetching headlines for category: $category")
            val response = newsApi.getBreakingNews(category = category)
            Log.d("NewsRepository", "Response: ${response.articles.size} articles")
            Resource.Success(response.articles)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error fetching headlines", e)
            Resource.Error(message = "Failed to fetch news: ${e.message}")
        }
    }

    override suspend fun searchForNews(query: String): Resource<List<Article>> {
        return try {
            Log.d("NewsRepository", "Searching for: $query")
            val response = newsApi.searchForNews(query = query)
            Log.d("NewsRepository", "Response: ${response.articles.size} articles")
            Resource.Success(response.articles)
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error searching news", e)
            Resource.Error(message = "Failed to search news: ${e.message}")
        }
    }
}