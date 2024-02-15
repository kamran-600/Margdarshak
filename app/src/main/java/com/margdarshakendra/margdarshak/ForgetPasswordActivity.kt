package com.margdarshakendra.margdarshak

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.margdarshakendra.margdarshak.databinding.ActivityForgetPasswordBinding
import com.margdarshakendra.margdarshak.models.ForgetPasswordRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.ForgetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private val forgetPasswordViewModel by viewModels<ForgetPasswordViewModel>()
    private var forgetPasswordResponseSuccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueBtn.setOnClickListener {
            if (!validateEmail()) return@setOnClickListener

            val forgetPasswordRequest = ForgetPasswordRequest(binding.email.text.toString().trim())
            Log.d(TAG, forgetPasswordRequest.toString())

            forgetPasswordViewModel.forgetPasswordRequest(forgetPasswordRequest)

        }


        forgetPasswordViewModel.forgetPasswordResponseLiveData.observe(this) {
            binding.spinKit.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {
                    forgetPasswordResponseSuccess = true
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    binding.responseText.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.forgetPassCardColor, null))
                    binding.responseText.text = it.data.message
                    binding.responseText.visibility = View.VISIBLE
                    binding.text.visibility = View.GONE
                    binding.email.visibility = View.GONE
                    binding.frameLayout.visibility = View.GONE
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    binding.continueBtn.visibility = View.VISIBLE
                    binding.responseText.visibility = View.VISIBLE
                    binding.responseText.text = it.message
                    binding.responseText.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    binding.spinKit.visibility = View.VISIBLE
                    binding.continueBtn.visibility = View.GONE
                }
            }
        }


    }

    private fun validateEmail(): Boolean {
        if (binding.email.text?.trim().isNullOrEmpty()) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text!!.trim()).matches()) {
            Toast.makeText(this, "Please Enter Email in Correct Pattern", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        if (forgetPasswordResponseSuccess) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("registration_response", binding.responseText.text.toString())
            startActivity(intent)
            finishAffinity()
        }
    }
}