package com.rizky.flashnews.presentation.news_screen

import com.rizky.flashnews.domain.model.Article

sealed class NewsScreenEvent {
    data class OnNewsCardClicked(val article: Article): NewsScreenEvent()
    data class OnCategoryChanged(val category: String): NewsScreenEvent()
    data class OnSearchQueryChanged(val searchQuery: String): NewsScreenEvent()
    object OnSearchIconClicked: NewsScreenEvent()
    object OnCloseIconClicked: NewsScreenEvent()
    object OnViewAllClicked : NewsScreenEvent()
    object OnViewAllClosed : NewsScreenEvent()
}