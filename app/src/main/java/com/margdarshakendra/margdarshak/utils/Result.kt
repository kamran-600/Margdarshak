package com.margdarshakendra.margdarshak.utils

import androidx.lifecycle.LiveData

sealed class Result<T>(val data: LiveData<T>? = null, var message: String? = null) {

    class Success<T>(data: LiveData<T>) : Result<T>(data)
    class Error<T>(message: String?, data: LiveData<T>? = null) : Result<T>(data, message)
    class Loading<T> : Result<T>()


}
