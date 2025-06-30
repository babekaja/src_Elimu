package com.kotlingdgocucb.elimuApp.data.datasource.remote

import com.kotlingdgocucb.elimuApp.domain.utils.Constants
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkModule {
    
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    }
                )
            }
            
            install(Logging) {
                level = LogLevel.BODY
                logger = Logger.DEFAULT
            }
            
            install(HttpTimeout) {
                requestTimeoutMillis = Constants.NETWORK_TIMEOUT
                connectTimeoutMillis = Constants.NETWORK_TIMEOUT
                socketTimeoutMillis = Constants.NETWORK_TIMEOUT
            }
            
            install(DefaultRequest) {
                headers.append("Content-Type", "application/json")
            }
            
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
        }
    }
}