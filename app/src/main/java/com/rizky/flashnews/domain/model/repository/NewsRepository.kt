package com.rizky.flashnews.domain.model.repository

import com.rizky.flashnews.domain.model.Article
import com.rizky.flashnews.util.Resource

interface NewsRepository {

    suspend fun getTopHeadlines(
        category: String
    ): Resource<List<Article>>

    suspend fun searchForNews(
        query: String
    ): Resource<List<Article>>
}