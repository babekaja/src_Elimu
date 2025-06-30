package com.kotlingdgocucb.elimuApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlingdgocucb.elimuApp.domain.utils.NetworkResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _error.value = exception.message ?: "An unexpected error occurred"
        _isLoading.value = false
    }
    
    protected fun <T> executeWithLoading(
        action: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        onError: (String) -> Unit = { _error.value = it }
    ) {
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = action()
                onSuccess(result)
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    protected fun <T> handleNetworkResult(
        result: NetworkResult<T>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit = { _error.value = it },
        onLoading: () -> Unit = { _isLoading.value = true }
    ) {
        when (result) {
            is NetworkResult.Success -> {
                _isLoading.value = false
                onSuccess(result.data)
            }
            is NetworkResult.Error -> {
                _isLoading.value = false
                onError(result.message)
            }
            is NetworkResult.Loading -> {
                onLoading()
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}