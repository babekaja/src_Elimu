package com.kotlingdgocucb.elimuApp.data.repository

import android.util.Log
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.ElimuDao
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Video
import com.kotlingdgocucb.elimuApp.domain.utils.Constants
import com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val elimuDao: ElimuDao
) : VideoRepository {

    companion object {
        private const val TAG = "VideoRepository"
    }

    override suspend fun getAllVideos(): Flow<NetworkResult<List<Video>>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            // Emit cached data first
            val localVideos = elimuDao.getAllVideos()
            if (localVideos.isNotEmpty()) {
                emit(NetworkResult.Success(localVideos))
            }
            
            // Fetch from network
            val response: HttpResponse = client.get("${Constants.BASE_API_URL}/videos/videos/")
            val networkVideos: List<Video> = response.body()
            
            // Update local cache
            elimuDao.insertVideos(networkVideos)
            
            // Emit fresh data
            emit(NetworkResult.Success(networkVideos))
            
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching videos", e)
            val localVideos = elimuDao.getAllVideos()
            if (localVideos.isNotEmpty()) {
                emit(NetworkResult.Success(localVideos))
            } else {
                emit(NetworkResult.Error("Failed to load videos: ${e.message}"))
            }
        }
    }

    override suspend fun getVideoById(id: Int): NetworkResult<Video> {
        return try {
            // Try local first
            val localVideo = elimuDao.getVideoById(id)
            
            // Fetch from network
            val response: HttpResponse = client.get("${Constants.BASE_API_URL}/videos/$id")
            val networkVideo: Video = response.body()
            
            // Update local cache
            elimuDao.insertVideo(networkVideo)
            
            NetworkResult.Success(networkVideo)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching video $id", e)
            val localVideo = elimuDao.getVideoById(id)
            if (localVideo != null) {
                NetworkResult.Success(localVideo)
            } else {
                NetworkResult.Error("Video not found: ${e.message}")
            }
        }
    }
    
    override suspend fun getVideosByTrack(track: String): Flow<NetworkResult<List<Video>>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            val allVideos = getAllVideos()
            allVideos.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val filteredVideos = result.data.filter { 
                            it.category.equals(track, ignoreCase = true) 
                        }
                        emit(NetworkResult.Success(filteredVideos))
                    }
                    is NetworkResult.Error -> emit(result)
                    is NetworkResult.Loading -> emit(result)
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Failed to filter videos: ${e.message}"))
        }
    }
    
    override suspend fun getPopularVideos(): Flow<NetworkResult<List<Video>>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            val allVideos = getAllVideos()
            allVideos.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val popularVideos = result.data
                            .filter { it.stars > 3.5f }
                            .sortedByDescending { it.stars }
                        emit(NetworkResult.Success(popularVideos))
                    }
                    is NetworkResult.Error -> emit(result)
                    is NetworkResult.Loading -> emit(result)
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Failed to get popular videos: ${e.message}"))
        }
    }
}