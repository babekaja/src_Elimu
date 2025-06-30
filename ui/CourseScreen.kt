package com.kotlingdgocucb.elimuApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.kotlingdgocucb.elimuApp.domain.model.User
import com.kotlingdgocucb.elimuApp.domain.utils.UiState
import com.kotlingdgocucb.elimuApp.ui.components.*
import com.kotlingdgocucb.elimuApp.ui.viewmodel.VideoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    navController: NavController,
    userInfo: User?,
    videoViewModel: VideoViewModel = koinViewModel()
) {
    val videosState by videoViewModel.videos.collectAsState()
    val popularVideosState by videoViewModel.popularVideos.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }
    
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val coroutineScope = rememberCoroutineScope()
    
    // Initialize popular videos
    LaunchedEffect(Unit) {
        videoViewModel.fetchPopularVideos()
    }
    
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                isRefreshing = true
                videoViewModel.refreshVideos()
                coroutineScope.launch {
                    kotlinx.coroutines.delay(1000)
                    isRefreshing = false
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search Bar
                val suggestions = remember(searchQuery, videosState) {
                    if (searchQuery.isNotEmpty()) {
                        videoViewModel.searchVideos(searchQuery)
                    } else {
                        emptyList()
                    }
                }
                
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    suggestions = suggestions,
                    onSuggestionClick = { video ->
                        navController.navigate("videoDetail/${video.id}")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Popular Videos Section
                when (popularVideosState) {
                    is UiState.Loading -> {
                        LoadingComponent(
                            modifier = Modifier.height(200.dp),
                            message = "Loading popular videos..."
                        )
                    }
                    is UiState.Success -> {
                        if (popularVideosState.data.isNotEmpty()) {
                            SectionTitle(
                                title = "Popular Videos",
                                onSeeMore = { navController.navigate("screenVideoPopulare") }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                items(popularVideosState.data.take(5)) { video ->
                                    VideoCard(
                                        video = video,
                                        onClick = { navController.navigate("videoDetail/${video.id}") },
                                        modifier = Modifier.width(280.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    is UiState.Error -> {
                        ErrorComponent(
                            message = popularVideosState.message,
                            onRetry = { videoViewModel.fetchPopularVideos() },
                            modifier = Modifier.height(100.dp)
                        )
                    }
                    is UiState.Empty -> {
                        EmptyStateComponent(
                            message = "No popular videos available",
                            modifier = Modifier.height(100.dp)
                        )
                    }
                }
                
                // All Videos Section
                SectionTitle(
                    title = "All Courses",
                    onSeeMore = { 
                        userInfo?.track?.let { track ->
                            navController.navigate("screenVideoTrack/$track")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                when (videosState) {
                    is UiState.Loading -> {
                        LoadingComponent(
                            message = "Loading courses...",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is UiState.Success -> {
                        val filteredVideos = if (searchQuery.isNotEmpty()) {
                            suggestions
                        } else {
                            // Filter by user's track and mentor
                            videosState.data.filter { video ->
                                video.category.equals(userInfo?.track, ignoreCase = true) &&
                                video.mentor_email == userInfo?.mentorEmail
                            }.sortedBy { it.order.toIntOrNull() ?: 0 }
                        }
                        
                        if (filteredVideos.isEmpty()) {
                            EmptyStateComponent(
                                message = if (searchQuery.isNotEmpty()) {
                                    "No videos found for \"$searchQuery\""
                                } else {
                                    "No courses available for your track"
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            if (isTablet) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    items(filteredVideos) { video ->
                                        VideoCard(
                                            video = video,
                                            onClick = { navController.navigate("videoDetail/${video.id}") }
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    items(filteredVideos) { video ->
                                        VideoListItem(
                                            video = video,
                                            onClick = { navController.navigate("videoDetail/${video.id}") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        ErrorComponent(
                            message = videosState.message,
                            onRetry = { videoViewModel.refreshVideos() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is UiState.Empty -> {
                        EmptyStateComponent(
                            message = "No courses available",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    onSeeMore: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        onSeeMore?.let { action ->
            TextButton(onClick = action) {
                Text(
                    text = "See more",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}