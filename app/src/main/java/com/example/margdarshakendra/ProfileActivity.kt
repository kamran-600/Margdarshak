package com.example.margdarshakendra

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.margdarshakendra.databinding.ActivityProfileBinding
import com.example.margdarshakendra.models.CountryRequest
import com.example.margdarshakendra.models.DistrictRequest
import com.example.margdarshakendra.models.UserUpdateRequest
import com.example.margdarshakendra.utils.Constants
import com.example.margdarshakendra.utils.Constants.TAG
import com.example.margdarshakendra.utils.Constants.USEREMAIL
import com.example.margdarshakendra.utils.Constants.USERMOBILE
import com.example.margdarshakendra.utils.Constants.USERNAME
import com.example.margdarshakendra.utils.Constants.USERTYPE
import com.example.margdarshakendra.utils.NetworkResult
import com.example.margdarshakendra.utils.SharedPreference
import com.example.margdarshakendra.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!

    /*lateinit var profileRepository: ProfileRepository
    lateinit var factory: ProfileViewModelFactory*/
    private val profileViewModel by viewModels<ProfileViewModel>()

    @Inject
    lateinit var sharedPreference: SharedPreference

    private var captureImageUri: Uri? = null

    private var countryCode : String? = null
    private var districtId = 0

    private var gender : String? = null

    private var profileImageUri : Uri? = null
    private var profileImageFile :File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeWithLoginData()


        getDistrict()
        getCountry()
        getPincode()
        setDOB()
        setProfilePicture()


        profileViewModel.districtResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    val districtNameMap = HashMap<String, Int>()

                    for (i in it.data!!.data) {
                        districtNameMap[i.district] = i.districtID
                    }
                    val districtAdapter = ArrayAdapter(
                        this, android.R.layout.simple_spinner_dropdown_item,
                        districtNameMap.keys.toList()
                    )
                    districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.districtAutoCompleteTextView.setAdapter(districtAdapter)
                    Log.d(TAG, it.data.toString())

                    binding.districtAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        districtId = districtNameMap[binding.districtAutoCompleteTextView.text.toString()]!!
                        Log.d(TAG, districtId.toString())
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        profileViewModel.countryResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    val countryNameMap = HashMap<String,String>()

                    for (i in it.data!!.data) {
                        countryNameMap[i.country] = i.country_code
                    }
                    val countryAdapter = ArrayAdapter(
                        this, android.R.layout.simple_spinner_dropdown_item,
                        countryNameMap.keys.toList()
                    )
                    countryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.countryAutoCompleteTextView.setAdapter(countryAdapter)
                    Log.d(TAG, it.data.toString())

                    binding.countryAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        countryCode = countryNameMap[binding.countryAutoCompleteTextView.text.toString()]!!
                        Log.d(TAG, countryCode.toString())
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        profileViewModel.pincodeResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    val postOfficeMap = HashMap<String, String>()

                    for (i in it.data!!.pincode) {
                        postOfficeMap[i.postoffice] = i.pincode
                    }
                    val postOfficeListAdapter = ArrayAdapter(
                        this, android.R.layout.simple_spinner_dropdown_item,
                        postOfficeMap.keys.toList()
                    )
                    postOfficeListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.postOfficeAutoCompleteView.setAdapter(postOfficeListAdapter)
                    Log.d(TAG, it.data.toString())

                    binding.postOfficeAutoCompleteView.setOnItemClickListener { _, _, _, _ ->
                        binding.pinCode.setText(
                            postOfficeMap[binding.postOfficeAutoCompleteView.text.toString()]
                        )
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {

                }
            }


        }

        profileViewModel.userUpdateResponseLiveData.observe(this){
            when(it){
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    sharedPreference.saveDetail(Constants.PROFILE_UPDATED, true, "Boolean")

                    startActivity(Intent(this, DashboardActivity::class.java))
                  //  finishAffinity()

                }
                is NetworkResult.Error ->{
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }
                is NetworkResult.Loading -> {

                }
            }
        }



        binding.registerBtn.setOnClickListener {

            val userType = getSelectedUserType()

            if(binding.radioGroup.checkedRadioButtonId == R.id.maleBtn){
                gender = "M"
            }
            else if(binding.radioGroup.checkedRadioButtonId == R.id.femaleBtn){
                gender = "F"
            }
            else {
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

             if(profileImageFile == null || countryCode == null){
                return@setOnClickListener
            }

            val userUpdateRequest = UserUpdateRequest(binding.institute.text.toString().trim(),districtId, binding.pinCode.text.toString().trim().toInt(), countryCode!!, binding.classorexam.text.toString().trim(), binding.email.text.toString().trim(), binding.dob.text.toString().trim(), profileImageFile!! , binding.details.text.toString().trim() ,gender!! , binding.qualification.text.toString().trim(), binding.specialization.text.toString().trim(), binding.phoneNo.text.toString().trim(), userType!!, binding.userName.text.toString().trim(), binding.preflanguage.text.toString().trim())
            profileViewModel.updateUserDetails(userUpdateRequest)


        }

    }

    private fun getImageFile(imageUri: Uri) {

        val cursor = contentResolver.query(imageUri, null, null, null, null)
        if(cursor != null){
            val fileIndex = cursor.getColumnIndex("_data")
            cursor.moveToFirst()
            profileImageFile = File(cursor.getString(fileIndex))
            Log.d(TAG, profileImageFile.toString())
            cursor.close()
        }

    }


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


    private fun getPincode() {
        //val pincodeRequest = PincodeRequest("delhi")
        profileViewModel.getPincode(binding.districtAutoCompleteTextView.text.toString())
    }


    private fun setProfilePicture() {

        binding.cameraImg.setOnClickListener {
            val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ENGLISH).format(Date())
            val value = ContentValues()
            value.put(MediaStore.Images.Media.DISPLAY_NAME, timeStamp)
            value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            captureImageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)!!

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                cameraLauncher.launch(captureImageUri)
            }

        }

        binding.galleryImg.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.clear.setOnClickListener {
            binding.imageCard.visibility = View.GONE
            captureImageUri = null
            profileImageUri = null
            binding.imageCardImage.setImageURI(null)
        }
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                cameraLauncher.launch(captureImageUri)
            } else {
                Toast.makeText(
                    this,
                    "Camera Permission Denied \nTo Allow Permission go to\n Setting < App Manager / App Permission",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            binding.imageCardImage.setImageURI(captureImageUri)

            captureImageUri?.let { it1 -> getImageFile(it1)
                profileImageUri = it1
                 }


            binding.imageCard.visibility = View.VISIBLE

            binding.imageCard.setOnClickListener { _ ->
                val intent = Intent(Intent.ACTION_VIEW, captureImageUri)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { galleryImageUri ->
            if (galleryImageUri != null) {
                profileImageUri = galleryImageUri
                getImageFile(galleryImageUri)
                binding.imageCardImage.setImageURI(galleryImageUri)
                binding.imageCard.visibility = View.VISIBLE
                binding.imageCard.setOnClickListener { _ ->
                    val help: Array<String> =
                        galleryImageUri.toString().split("media".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val uri = Uri.parse(
                        help[0] + "media/external/images/media" + help[help.size - 1]
                    )
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDOB() {
        binding.dob.setOnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action === MotionEvent.ACTION_UP) {
                if (event.rawX <= binding.dob.right + binding.dob.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                ) {
                    val calendar = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        this,
                        { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                            var month = month
                            val dob = String.format(
                                Locale.ENGLISH,
                                "%4d-%2d-%2d",
                                year,
                                ++month,
                                dayOfMonth
                            )
                            binding.dob.setText(dob)
                        },
                        calendar[Calendar.YEAR],
                        calendar[Calendar.MONTH],
                        calendar[Calendar.DAY_OF_MONTH]
                    )
                    datePickerDialog.show()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun getDistrict() {

        val districtRequest = DistrictRequest("getDistrict")
        profileViewModel.getDistrict(districtRequest)

    }

    private fun getCountry() {

        val countryRequest = CountryRequest("getCountry")
        profileViewModel.getCountry(countryRequest)

    }

    /*private fun getViewModel(): ProfileViewModel {

        RetrofitInstance.application = applicationContext as Application

        val addressSearchApi = RetrofitInstance.addressSearchApi

        profileRepository = ProfileRepository(addressSearchApi)

        factory = ProfileViewModelFactory(profileRepository)

        return ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }*/

    private fun initializeWithLoginData() {
        /*val intent = intent
        if (intent != null) {
            setUserTypeSpinnerAdapter()
            if (intent.getStringExtra("userType") == "S") {
                binding.userTypeSpinner.setSelection(1, true)
            } else {
                binding.userTypeSpinner.setSelection(2, true)
            }

            binding.userName.setText(intent.getStringExtra("name"))
            binding.email.setText(intent.getStringExtra("email"))
            binding.phoneNo.setText(intent.getStringExtra("mobile"))
        }*/
        setUserTypeSpinnerAdapter()
        if ( sharedPreference.getDetail(USERTYPE, "String") == "S") {
            binding.userTypeSpinner.setSelection(1, true)
            binding.specialization.visibility = View.GONE
            binding.qualification.visibility = View.GONE
        } else {
            binding.userTypeSpinner.setSelection(2, true)
            binding.classorexam.visibility = View.GONE
            binding.institute.visibility = View.GONE
        }

        binding.userName.setText(sharedPreference.getDetail(USERNAME, "String") as String)
        binding.email.setText(sharedPreference.getDetail(USEREMAIL, "String") as String)
        binding.phoneNo.setText(sharedPreference.getDetail(USERMOBILE, "String") as String)

    }

    private fun setUserTypeSpinnerAdapter() {
        val userTypeAdapter =
            ArrayAdapter.createFromResource(this, R.array.userType, R.layout.spinner_item)
        userTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.userTypeSpinner.adapter = userTypeAdapter

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}