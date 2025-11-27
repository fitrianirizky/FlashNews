package com.rizky.flashnews.presentation.news_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizky.flashnews.domain.model.repository.NewsRepository
import com.rizky.flashnews.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsScreenViewModel @Inject constructor (
    private val newsRepository: NewsRepository
): ViewModel() {

    var state by mutableStateOf(NewsScreenState())

    private var searchJob: Job? = null

    init {
        getNewsArticles(category = "general")
    }

    fun onEvent(event: NewsScreenEvent){
        when(event){
            is NewsScreenEvent.OnCategoryChanged -> {
                state = state.copy(
                    category = event.category,
                    isViewAllMode = false
                )
                getNewsArticles(state.category)
            }
            is NewsScreenEvent.OnNewsCardClicked -> {
                state = state.copy(selectedArticle = event.article)
            }
            NewsScreenEvent.OnSearchIconClicked -> {
                state = state.copy(
                    isSearchBarVisible = true,
                    articles = emptyList() // Kosongkan list saat mulai search
                )
            }
            NewsScreenEvent.OnCloseIconClicked -> {
                state = state.copy(isSearchBarVisible = false)
                // Kembalikan ke berita awal saat search ditutup
                getNewsArticles(category = state.category)
            }
            is NewsScreenEvent.OnSearchQueryChanged -> {
                state = state.copy(searchQuery = event.searchQuery)

                // Batalkan pencarian sebelumnya jika user masih mengetik
                searchJob?.cancel()

                // Logic Debounce yang Benar
                searchJob = viewModelScope.launch {
                    delay(500L) // Tunggu 0.5 detik setelah user berhenti mengetik
                    if (state.searchQuery.isNotEmpty()) {
                        searchForNews(query = state.searchQuery)
                    }
                }
            }
            NewsScreenEvent.OnViewAllClicked -> {
                state = state.copy(isViewAllMode = true)
            }
            NewsScreenEvent.OnViewAllClosed -> {
                state = state.copy(isViewAllMode = false)
            }
        }
    }

    private fun getNewsArticles(category: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = newsRepository.getTopHeadlines(category = category)
            when (result) {
                is Resource.Success -> {
                    state = state.copy(
                        articles = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message,
                        isLoading = false,
                        articles = emptyList()
                    )
                }
            }
        }
    }

    private fun searchForNews(query: String) {
        // Jangan search jika query kosong (menghindari request tak perlu)
        if (query.isBlank()) return

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null) // Reset error saat mulai search
            val result = newsRepository.searchForNews(query = query)
            when (result) {
                is Resource.Success -> {
                    state = state.copy(
                        articles = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message,
                        isLoading = false,
                        articles = emptyList()
                    )
                }
            }
        }
    }
}