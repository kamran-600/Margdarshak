package com.example.margdarshakendra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.margdarshakendra.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)





    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}