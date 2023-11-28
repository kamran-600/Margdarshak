package com.example.margdarshakendra

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.margdarshakendra.broadcastReceivers.SmsBroadcastReceiver
import com.example.margdarshakendra.databinding.ActivityRegisterBinding
import com.example.margdarshakendra.models.OtpRequest
import com.example.margdarshakendra.models.RegisterRequest
import com.example.margdarshakendra.utils.Constants.TAG
import com.example.margdarshakendra.utils.NetworkResult
import com.example.margdarshakendra.viewmodels.RegisterViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel by viewModels<RegisterViewModel>()
   // private lateinit var factory: RegisterViewModelFactory
   // private lateinit var registerRepository: RegisterRepository
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    private var serverOtp = 0
    private var vMobile = "N"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /*binding.phoneNo.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (validateIndianNumber()) {
                    binding.ccp.registerCarrierNumberEditText(binding.phoneNo)
                    val otpRequest = OtpRequest(binding.ccp.fullNumber)
                    Log.d(TAG, otpRequest.toString())
                    registerViewModel.sendOtp(otpRequest)
                    return false   // Focus will change according to the actionId  if true then acc to logic
                }
                return true

            }

        })*/

        setUserTypeSpinnerAdapter()

       // registerViewModel = getViewModel()

        binding.registerBtn.setOnClickListener {

            if (vMobile != "Y") {
                Toast.makeText(this, "Phone number is not verified", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!validateDetails()) return@setOnClickListener

            val registerRequest = RegisterRequest(
                binding.ccp.selectedCountryCode,
                binding.email.text.toString().trim(),
                binding.phoneNo.text.toString().trim(),
                binding.userName.text.toString().trim(),
                getSelectedUserType()!!,
                vMobile
            )
            Log.d(TAG, registerRequest.toString())
            registerViewModel.registerUser(
                registerRequest
            )

        }

        registerViewModel.registerResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Toast.makeText(this, it.data!!.message, Toast.LENGTH_LONG).show()
                    binding.errorMessage.visibility = GONE
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = it.message
                }

                is NetworkResult.Loading -> {
                    //Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                }
            }

            /*if (it.success) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                binding.errorMessage.visibility = View.GONE
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()

            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = it.message
            }
            Log.d(TAG, "activity " + it.message)*/
        }

        registerViewModel.otpResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    serverOtp = it.data!!.otp
                    Toast.makeText(this, "Otp Sent Successfully", Toast.LENGTH_LONG).show()
                    binding.otpErrorMessage.visibility = GONE
                    startSmsUserConsent()
                }

                is NetworkResult.Error -> {
                    if (it.message == "existing user !") {
                        it.message = "Mobile already exists!"
                    }
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    binding.otpErrorMessage.visibility = View.VISIBLE
                    binding.otpErrorMessage.text = it.message
                }

                is NetworkResult.Loading -> {
                    //Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
                }

            }
        }

        binding.phoneNo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && validatePhoneNumber()) {
                binding.ccp.registerCarrierNumberEditText(binding.phoneNo)
                val otpRequest = OtpRequest(binding.ccp.fullNumber.trim())
                Log.d(TAG, otpRequest.toString())
                registerViewModel.sendOtp(otpRequest)
            }
        }

        requestNextFocusInOTP()

        binding.verifySmsCodeBtn.setOnClickListener {
            if (serverOtp != 0 && getOtpVerified() && binding.verifySmsCodeBtn.text != "Verified") {
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()
                binding.verifySmsCodeBtn.setBackgroundColor(getColor(R.color.green))
                binding.verifySmsCodeBtn.text = "Verified"
                binding.phoneNo.isEnabled = false
                binding.ccp.setCcpClickable(false)
                if(smsBroadcastReceiver != null){
                    unregisterReceiver(smsBroadcastReceiver)
                    smsBroadcastReceiver = null
                }
            }

        }

        binding.loginTxt.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN,0,0)
            }
            else overridePendingTransition(0,0)
            finishAffinity()
        }

    }


    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsUserConsent(null)
        task.addOnSuccessListener {
            Log.d(TAG, "bCR is started success")
            if(smsBroadcastReceiver != null){
                unregisterReceiver(smsBroadcastReceiver)
                smsBroadcastReceiver = null
            }
            registerBroadcastReceiver()
        }
        task.addOnFailureListener{
            Log.d(TAG, it.message.toString())
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerBroadcastReceiver() {

        smsBroadcastReceiver = SmsBroadcastReceiver()

        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onOtpReceived(intent: Intent?) {
                    if (intent != null) {
                        smsBroadcastReceiverIntent.launch(intent)
                    }
                }
                override fun onFailure() {
                      unregisterReceiver(smsBroadcastReceiver)
                      smsBroadcastReceiver = null
                      Toast.makeText(this@RegisterActivity, "Failed to launch sms intent", Toast.LENGTH_SHORT).show()
                }

            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(smsBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
            } else registerReceiver(
                smsBroadcastReceiver,
                intentFilter,
                RECEIVER_VISIBLE_TO_INSTANT_APPS
            )

        } else registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    val smsBroadcastReceiverIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            val message = it.data!!.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            if (message != null) {
                getOtpFromMessage(message)
            }

        }
        else Toast.makeText(this, "error in intent", Toast.LENGTH_SHORT).show()
    }


    private fun getOtpFromMessage(message: String) {
        val otpPatterns = Pattern.compile("(|^)\\d{4}")
        val matcher = otpPatterns.matcher(message)
        if (matcher.find()) {
            val otp = matcher.group(0)
            if (otp != null) {
                binding.otp1.setText(otp.toCharArray()[0].toString())
                binding.otp2.setText(otp.toCharArray()[1].toString())
                binding.otp3.setText(otp.toCharArray()[2].toString())
                binding.otp4.setText(otp.toCharArray()[3].toString())
                unregisterReceiver(smsBroadcastReceiver)
                smsBroadcastReceiver = null
            }
        }

    }

    private fun setUserTypeSpinnerAdapter() {
        val userTypeAdapter =
            ArrayAdapter.createFromResource(this, R.array.userType, R.layout.spinner_item)
        userTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.userTypeSpinner.adapter = userTypeAdapter

    }

    /*private fun getViewModel(): RegisterViewModel {

        val userApi = RetrofitInstance.userApi

        registerRepository = RegisterRepository(userApi)

        factory = RegisterViewModelFactory(registerRepository)

        return ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }*/

    private fun getSelectedUserType(): String? {
        return when (binding.userTypeSpinner.selectedItemPosition) {
            0 -> {
                Toast.makeText(this, "Please Select User Type", Toast.LENGTH_SHORT).show()
                null
            }
            1 -> {
                "S"
            }
            else -> "A"
        }
    }

    private fun validateDetails(): Boolean {
        if (!validatePhoneNumber()) {
            return false
        } else if (TextUtils.isEmpty(binding.phoneNo.text) || TextUtils.isEmpty(binding.email.text) || TextUtils.isEmpty(
                binding.userName.text
            )
        ) {
            Toast.makeText(this, "Please fill required details", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
            Toast.makeText(this, "Please enter email in correct pattern", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (getSelectedUserType() == null) {
            return false
        } else if (!binding.agreeCheckBox.isChecked) {
            Toast.makeText(this, "Please agree to the terms & conditions", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (binding.ccp.selectedCountryCode == "91") {
            binding.phoneNo.maxEms = 10
            if (binding.phoneNo.text?.length != 10) {
                Toast.makeText(
                    this,
                    "Please fill 10 digit number if flag is Indian",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            return true
        } else return true
    }

    private fun validatePhoneNumber(): Boolean {
        if (binding.ccp.selectedCountryCode == "91") { // check if it is indian number then it must have 10 digits
            if (binding.phoneNo.text?.length != 10) {
                Toast.makeText(
                    this,
                    "Please fill 10 digit number if flag is Indian",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            return true
        } else return true
    }

    private fun getEnteredOtp(): Pair<Boolean, Int> {
        val otp =
            binding.otp1.text.toString() + binding.otp2.text + binding.otp3.text + binding.otp4.text

        if (TextUtils.isEmpty(binding.otp1.text) || TextUtils.isEmpty(binding.otp2.text) || TextUtils.isEmpty(
                binding.otp3.text
            ) || TextUtils.isEmpty(binding.otp4.text)
        ) {
            Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            return Pair(false, 0)
        }
        return Pair(true, otp.toInt())
    }

    private fun getOtpVerified(): Boolean {
        return if (getEnteredOtp().first && getEnteredOtp().second == serverOtp) {
            vMobile = "Y"
            true
        } else false
    }

    private fun requestNextFocusInOTP() {
        binding.otp1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.otp1.text?.length == 1)
                    binding.otp2.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.otp2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.otp2.text?.length == 1)
                    binding.otp3.requestFocus()
                else if (binding.otp2.text.isNullOrEmpty())
                    binding.otp1.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.otp3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.otp3.text?.length == 1)
                    binding.otp4.requestFocus()
                else if (binding.otp3.text.isNullOrEmpty())
                    binding.otp2.requestFocus()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.otp4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.otp4.text?.length == 1) {
                    binding.otp4.onEditorAction(EditorInfo.IME_ACTION_DONE)
                    binding.otp4.clearFocus()
                } else if (binding.otp4.text.isNullOrEmpty())
                    binding.otp3.requestFocus()

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }




    override fun onStop() {
        super.onStop()
        if(smsBroadcastReceiver != null){
            unregisterReceiver(smsBroadcastReceiver)
            smsBroadcastReceiver = null
        }
    }
    
    

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}


