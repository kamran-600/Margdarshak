package com.example.margdarshakendra

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.margdarshakendra.api.RetrofitInstance
import com.example.margdarshakendra.databinding.ActivityLoginBinding
import com.example.margdarshakendra.models.LoginRequest
import com.example.margdarshakendra.repository.LoginRepository
import com.example.margdarshakendra.utils.Constants.TAG
import com.example.margdarshakendra.utils.NetworkResult
import com.example.margdarshakendra.viewmodels.LoginViewModel
import com.example.margdarshakendra.viewmodels.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var factory: LoginViewModelFactory
    private lateinit var loginRepository: LoginRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = getViewModel()

        binding.loginBtn.setOnClickListener {

            if( ! validateDetails()) return@setOnClickListener

            val loginRequest =
                LoginRequest(binding.email.text.toString(), binding.password.text.toString())
            Log.d(TAG, loginRequest.toString())
            loginViewModel.loginUser(loginRequest)

        }

        loginViewModel.loginResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Toast.makeText(
                        this,
                        it.data?.message.plus(" login Successful") ?: "null",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.errorMessage.visibility = View.GONE
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finishAffinity()
                }

                is NetworkResult.Error -> {
                    if(it.message == "Unauthorized"){
                        it.message = "Password does not match !"
                    }
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = it.message
                }

                is NetworkResult.Loading -> {
                    //Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.registerTxt.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN,0,0)
            }
            else overridePendingTransition(0,0)
            finishAffinity()
        }

    }

    private fun validateDetails() : Boolean {

        if (TextUtils.isEmpty(binding.email.text) || TextUtils.isEmpty(binding.password.text)) {
            Toast.makeText(this, "Please enter required details", Toast.LENGTH_SHORT).show()
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = "Please enter required details !"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {

            Toast.makeText(this, "Please enter email in correct pattern", Toast.LENGTH_SHORT).show()
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = "Please enter email in correct pattern !"
            return false
        }
        else if( ! binding.agreeCheckBox.isChecked){
            Toast.makeText(this, "Please agree to the terms & conditions", Toast.LENGTH_SHORT).show()
            return false
        }
        else return true

    }

    private fun getViewModel(): LoginViewModel {

        val userApi = RetrofitInstance.api

        loginRepository = LoginRepository(userApi)

        factory = LoginViewModelFactory(loginRepository)

        return ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}