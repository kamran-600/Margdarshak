package com.margdarshakendra.margdarshak

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.margdarshakendra.margdarshak.databinding.ActivityLoginBinding
import com.margdarshakendra.margdarshak.models.LoginRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.utils.TokenManager
import com.margdarshakendra.margdarshak.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(tokenManager.getToken() != null){
            Log.d(TAG, tokenManager.getToken()!!)
            if(sharedPreference.getDetail(Constants.PROFILE_UPDATED, "Boolean") == true){
                startActivity(Intent(this, DashboardActivity::class.java))
            }
            else startActivity(Intent(this, ProfileActivity::class.java))
            finishAffinity()
        }


        binding.loginBtn.setOnClickListener {

            if( ! validateDetails()) return@setOnClickListener

            val loginRequest =
                LoginRequest(binding.email.text.toString().trim(), binding.password.text.toString().trim())
            Log.d(TAG, loginRequest.toString())
            loginViewModel.loginUser(loginRequest)

        }

       /* val progressBar : ProgressBar = binding.spinKit

        progressBar.indeterminateDrawable = DoubleBounce()
*/


        loginViewModel.loginResponseLiveData.observe(this) {
            binding.spinKit.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)

                    Log.d(TAG, it.data.toString())
                    Toast.makeText(
                        this,
                        it.data.message.plus(" login Successful"),
                        Toast.LENGTH_LONG
                    ).show()
                    binding.errorMessage.visibility = View.GONE

                    sharedPreference.saveDetail(Constants.PROFILE_UPDATED, it.data.profile_updated , "Boolean")
                    sharedPreference.saveDetail(Constants.USEREMAIL, it.data.email , "String")
                    sharedPreference.saveDetail(Constants.USERMOBILE,it.data.mobile , "String")
                    sharedPreference.saveDetail(Constants.USERTYPE,it.data.usertype , "String")
                    sharedPreference.saveDetail(Constants.USERNAME,it.data.name , "String")
                    sharedPreference.saveDetail(Constants.USERLOGINID,it.data.login_id , "Int")



                    if(it.data.profile_updated){
                        startActivity(Intent(this, DashboardActivity::class.java))
                    }
                    else {
                        val intent = Intent(this, ProfileActivity::class.java)
                        /*intent.putExtra("email",it.data.email)
                        intent.putExtra("mobile",it.data.mobile)
                        intent.putExtra("userType",it.data.usertype)
                        intent.putExtra("name",it.data.name)*/
                        startActivity(intent)
                    }
                    finishAffinity()
                }

                is NetworkResult.Error -> {
                    binding.loginBtn.visibility = View.VISIBLE
                    if(it.message == "Unauthorized"){
                        it.message = "Password does not match !"
                    }
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = it.message
                }

                is NetworkResult.Loading -> {
                    binding.spinKit.visibility = View.VISIBLE
                    binding.loginBtn.visibility = View.INVISIBLE
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

        if (TextUtils.isEmpty(binding.email.text?.trim()) || TextUtils.isEmpty(binding.password.text?.trim())) {
            Toast.makeText(this, "Please enter required details", Toast.LENGTH_SHORT).show()
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = "Please enter required details !"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString().trim()).matches()) {

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

    /*private fun getViewModel(): LoginViewModel {

        val userApi = RetrofitInstance.userApi

        loginRepository = LoginRepository(userApi)

        factory = LoginViewModelFactory(loginRepository)

        return ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}