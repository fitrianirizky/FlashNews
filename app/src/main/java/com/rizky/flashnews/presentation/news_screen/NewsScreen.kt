package com.rizky.flashnews.presentation.news_screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.rizky.flashnews.domain.model.Article
import com.rizky.flashnews.presentation.component.BottomSheetContent
import com.rizky.flashnews.presentation.component.CategoryTabRow
import com.rizky.flashnews.presentation.component.NewsArticleCard
import com.rizky.flashnews.presentation.component.TrendingNewsCard
import com.rizky.flashnews.presentation.component.NewsScreenTopBar
import com.rizky.flashnews.presentation.component.RetryContent
import com.rizky.flashnews.presentation.component.SearchAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource // Import Wajib
import com.rizky.flashnews.R // Import R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    state: NewsScreenState,
    onEvent: (NewsScreenEvent) -> Unit,
    onReadFullStoryButtonClicked: (String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val coroutineScope = rememberCoroutineScope()

    // DATA LOGIKA (Tetap bahasa Inggris untuk API request)
    val categories = listOf(
        "General", "Business", "Health", "Science", "Sports", "Technology", "Entertainment"
    )

    // DATA TAMPILAN (Menggunakan String Resource untuk UI)
    // Urutan harus sama persis dengan list 'categories' di atas
    val displayCategories = listOf(
        stringResource(R.string.general),
        stringResource(R.string.business),
        stringResource(R.string.health),
        stringResource(R.string.science),
        stringResource(R.string.sports),
        stringResource(R.string.technology),
        stringResource(R.string.entertainment)
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { categories.size }
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var shouldBottomSheetShow by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // --- BACK HANDLER LOGIC ---
    BackHandler(enabled = state.isSearchBarVisible) {
        onEvent(NewsScreenEvent.OnCloseIconClicked)
        focusManager.clearFocus()
    }

    BackHandler(enabled = state.isViewAllMode) {
        onEvent(NewsScreenEvent.OnViewAllClosed)
    }

    // --- BOTTOM SHEET ---
    if (shouldBottomSheetShow) {
        ModalBottomSheet(
            onDismissRequest = { shouldBottomSheetShow = false },
            sheetState = sheetState,
            content = {
                state.selectedArticle?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        BottomSheetContent(
                            article = it,
                            onReadFullStoryButtonClicked = {
                                onReadFullStoryButtonClicked(it.url)
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) shouldBottomSheetShow = false
                                }
                            }
                        )
                    }
                }
            }
        )
    }

    // --- EFFECT HANDLERS ---
    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Gunakan list 'categories' (Inggris) untuk logic API
            onEvent(NewsScreenEvent.OnCategoryChanged(category = categories[page]))
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (state.searchQuery.isNotEmpty()) {
            onEvent(NewsScreenEvent.OnSearchQueryChanged(searchQuery = state.searchQuery))
        }
    }

    // --- UI CONTENT ---
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = state.isSearchBarVisible, label = "SearchCrossfade") { isSearchVisible ->
                if (isSearchVisible) {
                    // --- MODE SEARCH ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        SearchAppBar(
                            modifier = Modifier.focusRequester(focusRequester),
                            value = state.searchQuery,
                            onInputValueChange = { newValue -> onEvent(NewsScreenEvent.OnSearchQueryChanged(newValue)) },
                            onCloseIconClicked = { onEvent(NewsScreenEvent.OnCloseIconClicked) },
                            onSearchIconClicked = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                        if (state.searchQuery.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(

                                    text = stringResource(id = R.string.type_to_search),

                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            SearchContentList(
                                state = state,
                                isLandscape = isLandscape,
                                onCardClicked = { article ->
                                    shouldBottomSheetShow = true
                                    onEvent(NewsScreenEvent.OnNewsCardClicked(article = article))
                                },
                                onRetry = { onEvent(NewsScreenEvent.OnSearchQueryChanged(state.searchQuery)) }
                            )
                        }
                    }
                } else {
                    // --- MODE NORMAL ---
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            NewsScreenTopBar(
                                scrollBehavior = scrollBehavior,
                                onSearchIconClicked = {
                                    coroutineScope.launch {
                                        delay(500)
                                        focusRequester.requestFocus()
                                    }
                                    onEvent(NewsScreenEvent.OnSearchIconClicked)
                                }
                            )
                        }
                    ) { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            // Mengirim 'displayCategories' ke UI agar tampilannya terjemahan
                            CategoryTabRow(
                                pagerState = pagerState,
                                categories = displayCategories,
                                onTabSelected = { index ->
                                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                }
                            )

                            HorizontalPager(state = pagerState) { page ->
                                // Mengambil kategori asli (Inggris) untuk logika pengecekan
                                val currentCategoryLogic = categories[page]

                                if (currentCategoryLogic == "General") {
                                    if (state.isViewAllMode) {
                                        SearchContentList(
                                            state = state,
                                            isLandscape = isLandscape,
                                            onCardClicked = { article ->
                                                shouldBottomSheetShow = true
                                                onEvent(NewsScreenEvent.OnNewsCardClicked(article = article))
                                            },
                                            onRetry = {
                                                onEvent(NewsScreenEvent.OnCategoryChanged(currentCategoryLogic))
                                            }
                                        )
                                    } else {
                                        HomeContentList(
                                            state = state,
                                            isLandscape = isLandscape,
                                            onCardClicked = { article ->
                                                shouldBottomSheetShow = true
                                                onEvent(NewsScreenEvent.OnNewsCardClicked(article = article))
                                            },
                                            onRetry = {
                                                onEvent(NewsScreenEvent.OnCategoryChanged(currentCategoryLogic))
                                            },
                                            onViewAllClicked = {
                                                onEvent(NewsScreenEvent.OnViewAllClicked)
                                            }
                                        )
                                    }
                                } else {
                                    SearchContentList(
                                        state = state,
                                        isLandscape = isLandscape,
                                        onCardClicked = { article ->
                                            shouldBottomSheetShow = true
                                            onEvent(NewsScreenEvent.OnNewsCardClicked(article = article))
                                        },
                                        onRetry = {
                                            onEvent(NewsScreenEvent.OnCategoryChanged(currentCategoryLogic))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------
// KOMPONEN 1: HomeContentList
// -----------------------------------------------------------------------
@Composable
fun HomeContentList(
    state: NewsScreenState,
    isLandscape: Boolean,
    onCardClicked: (Article) -> Unit,
    onRetry: () -> Unit,
    onViewAllClicked: () -> Unit
) {
    if (state.isLoading || state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> RetryContent(error = state.error, onRetry = onRetry)
            }
        }
        return
    }

    val breakingNews = state.articles.take(5)
    val trendingNews = state.articles.drop(5)

    LazyVerticalGrid(
        columns = if (isLandscape) GridCells.Fixed(2) else GridCells.Fixed(1),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Breaking News
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(
                    text = stringResource(id = R.string.breaking_news), // String Resource
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val breakingPagerState = rememberPagerState(initialPage = 0, pageCount = { breakingNews.size })
                HorizontalPager(
                    state = breakingPagerState,
                    contentPadding = PaddingValues(end = 16.dp),
                    pageSpacing = 8.dp
                ) { page ->
                    NewsArticleCard(
                        article = breakingNews[page],
                        onCardClicked = onCardClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                }
            }
        }

        // Section Trending News Header
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.trending_news), // String Resource
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.view_all), // String Resource
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onViewAllClicked() }
                )
            }
        }

        // Section Trending News List
        items(trendingNews) { article ->
            TrendingNewsCard(
                article = article,
                onClick = onCardClicked
            )
        }
    }
}

// -----------------------------------------------------------------------
// KOMPONEN 2: SearchContentList
// -----------------------------------------------------------------------
@Composable
fun SearchContentList(
    state: NewsScreenState,
    isLandscape: Boolean,
    onCardClicked: (Article) -> Unit,
    onRetry: () -> Unit
) {
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (state.error != null) { RetryContent(error = state.error, onRetry = onRetry); return }

    val articlesToShow = state.articles

    if (articlesToShow.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.no_news_found), // String Resource
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = if (isLandscape) GridCells.Fixed(2) else GridCells.Fixed(1),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(articlesToShow) { article ->
            TrendingNewsCard(
                article = article,
                onClick = onCardClicked
            )
        }
    }
}