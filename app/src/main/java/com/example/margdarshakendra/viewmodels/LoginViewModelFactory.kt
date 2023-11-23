package com.example.margdarshakendra.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.margdarshakendra.repository.LoginRepository
import kotlin.math.log

class LoginViewModelFactory(private val loginRepository: LoginRepository) :ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(loginRepository::class.java).newInstance(loginRepository)
    }
}