package com.kotlingdgocucb.elimuApp.domain.usecase

import com.kotlingdgocucb.elimuApp.data.repository.ProgressRepository
import com.kotlingdgocucb.elimuApp.domain.model.ProgressCreate
import com.kotlingdgocucb.elimuApp.domain.model.ProgressResponse

// ProgressUseCase.kt
class ProgressUseCase(private val repository: ProgressRepository) {

    suspend fun trackProgress(videoId: Int, menteeEmail: String): ProgressResponse? {
        val progress = ProgressCreate(videoId = videoId, menteeEmail = menteeEmail)
        return repository.addProgress(progress)
    }

    suspend fun retrieveProgress(videoId: Int, menteeEmail: String): ProgressResponse? {
        return repository.getProgress(videoId, menteeEmail)
    }
}