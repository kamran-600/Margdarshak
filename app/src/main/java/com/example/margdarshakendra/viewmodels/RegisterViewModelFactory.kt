package com.example.margdarshakendra.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.margdarshakendra.repository.RegisterRepository

class RegisterViewModelFactory(private val registerRepository: RegisterRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(registerRepository::class.java).newInstance(registerRepository)
    }


}