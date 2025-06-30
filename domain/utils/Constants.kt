package com.kotlingdgocucb.elimuApp.domain.utils

object Constants {
    // API URLs
    const val BASE_API_URL = "https://elimugdgocucb.onrender.com"
    const val FIREBASE_REALTIME_DB_URL = "https://elimu-be04d-default-rtdb.firebaseio.com/data/~2F"
    
    // Database
    const val DATABASE_NAME = "elimuApp.db"
    const val DATABASE_VERSION = 1
    
    // DataStore
    const val DATASTORE_NAME = "Setting"
    
    // Google Sign-In
    const val GOOGLE_CLIENT_ID = "547623569349-lvgrbermdvb5e8ssa021kgrh9nunfdk8.apps.googleusercontent.com"
    
    // YouTube
    const val YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi"
    const val YOUTUBE_WATCH_BASE_URL = "https://www.youtube.com/watch?v="
    const val YOUTUBE_EMBED_BASE_URL = "https://www.youtube.com/embed"
    
    // UI Constants
    const val ANIMATION_DURATION_SHORT = 300
    const val ANIMATION_DURATION_MEDIUM = 800
    const val ANIMATION_DURATION_LONG = 1000
    
    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    
    // Timeouts
    const val NETWORK_TIMEOUT = 30_000L
    const val SPLASH_DELAY = 2000L
}