package com.kotlingdgocucb.elimuApp.data.repository

import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Video
import com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    suspend fun getAllVideos(): Flow<NetworkResult<List<Video>>>
    suspend fun getVideoById(id: Int): NetworkResult<Video>
    suspend fun getVideosByTrack(track: String): Flow<NetworkResult<List<Video>>>
    suspend fun getPopularVideos(): Flow<NetworkResult<List<Video>>>
}