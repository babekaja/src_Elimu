package com.kotlingdgocucb.elimuApp.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.kotlingdgocucb.elimuApp.data.repository.VideoRepository
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Video
import com.kotlingdgocucb.elimuApp.domain.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class VideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : BaseViewModel() {

    private val _videos = MutableStateFlow<UiState<List<Video>>>(UiState.Loading)
    val videos: StateFlow<UiState<List<Video>>> = _videos.asStateFlow()

    private val _videoDetail = MutableStateFlow<UiState<Video>>(UiState.Loading)
    val videoDetail: StateFlow<UiState<Video>> = _videoDetail.asStateFlow()
    
    private val _popularVideos = MutableStateFlow<UiState<List<Video>>>(UiState.Loading)
    val popularVideos: StateFlow<UiState<List<Video>>> = _popularVideos.asStateFlow()

    init {
        fetchAllVideos()
    }

    fun fetchAllVideos() {
        viewModelScope.launch(exceptionHandler) {
            videoRepository.getAllVideos().collect { result ->
                _videos.value = when (result) {
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Success -> {
                        if (result.data.isEmpty()) UiState.Empty else UiState.Success(result.data)
                    }
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Error -> UiState.Error(result.message)
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Loading -> UiState.Loading
                }
            }
        }
    }

    fun fetchVideoById(id: Int) {
        viewModelScope.launch(exceptionHandler) {
            _videoDetail.value = UiState.Loading
            
            val result = videoRepository.getVideoById(id)
            _videoDetail.value = when (result) {
                is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Success -> UiState.Success(result.data)
                is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Error -> UiState.Error(result.message)
                is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Loading -> UiState.Loading
            }
        }
    }
    
    fun fetchVideosByTrack(track: String) {
        viewModelScope.launch(exceptionHandler) {
            videoRepository.getVideosByTrack(track).collect { result ->
                _videos.value = when (result) {
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Success -> {
                        if (result.data.isEmpty()) UiState.Empty else UiState.Success(result.data)
                    }
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Error -> UiState.Error(result.message)
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Loading -> UiState.Loading
                }
            }
        }
    }
    
    fun fetchPopularVideos() {
        viewModelScope.launch(exceptionHandler) {
            videoRepository.getPopularVideos().collect { result ->
                _popularVideos.value = when (result) {
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Success -> {
                        if (result.data.isEmpty()) UiState.Empty else UiState.Success(result.data)
                    }
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Error -> UiState.Error(result.message)
                    is com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult.Loading -> UiState.Loading
                }
            }
        }
    }
    
    fun refreshVideos() {
        fetchAllVideos()
    }
    
    fun searchVideos(query: String): List<Video> {
        return when (val state = _videos.value) {
            is UiState.Success -> state.data.filter { 
                it.title.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
            else -> emptyList()
        }
    }
}