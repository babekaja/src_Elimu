package com.kotlingdgocucb.elimuApp.domain.utils

import android.util.Patterns

object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 2
    }
    
    fun isValidTrack(track: String): Boolean {
        val validTracks = listOf("Web", "Flutter", "Kotlin", "Python", "AI")
        return track in validTracks
    }
    
    fun isValidRating(rating: Int): Boolean {
        return rating in 1..5
    }
    
    fun isValidComment(comment: String?): Boolean {
        return comment == null || comment.length <= 500
    }
    
    fun isValidYouTubeUrl(url: String): Boolean {
        val youtubePattern = "^[a-zA-Z0-9_-]{11}$"
        return url.matches(youtubePattern.toRegex())
    }
}