package com.margdarshakendra.margdarshak

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.margdarshakendra.margdarshak.databinding.ActivityProfileBinding
import com.margdarshakendra.margdarshak.models.CountryRequest
import com.margdarshakendra.margdarshak.models.DistrictRequest
import com.margdarshakendra.margdarshak.models.UserUpdateRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.Constants.USEREMAIL
import com.margdarshakendra.margdarshak.utils.Constants.USERMOBILE
import com.margdarshakendra.margdarshak.utils.Constants.USERNAME
import com.margdarshakendra.margdarshak.utils.Constants.USERTYPE
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
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

    private var countryCode: String? = null
    private var districtId = 0

    private var profileImageBase64: String? = null
    private lateinit var districtNameMap: HashMap<String, Int>
    private lateinit var countryNameMap: HashMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeWithLoginData()
        binding.userTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Please Select User Type",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        1 -> {
                            binding.specialization.visibility = GONE
                            binding.specialization.text = null
                            binding.qualification.visibility = GONE
                            binding.qualification.text = null
                            binding.classorexam.visibility = VISIBLE
                            binding.institute.visibility = VISIBLE
                        }

                        else -> {
                            binding.specialization.visibility = VISIBLE
                            binding.qualification.visibility = VISIBLE
                            binding.classorexam.visibility = GONE
                            binding.classorexam.text = null
                            binding.institute.visibility = GONE
                            binding.institute.text = null
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }


        getCountry()
        getDistrict()
        getPincode()
        setDOB()
        setProfilePicture()

        profileViewModel.countryResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    countryNameMap = HashMap()

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

                    /*binding.countryAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        // Log.d(TAG, countryCode.toString())
                    }*/
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        profileViewModel.districtResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    districtNameMap = HashMap()

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

                    /*binding.districtAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        // Log.d(TAG, districtId.toString())
                    }*/
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

        profileViewModel.userUpdateResponseLiveData.observe(this) {
            binding.spinKit.visibility = GONE
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    sharedPreference.saveDetail(Constants.PROFILE_UPDATED, true, "Boolean")
                    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finishAffinity()

                }

                is NetworkResult.Error -> {
                    binding.submitBtn.visibility = VISIBLE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    binding.spinKit.visibility = VISIBLE
                    binding.submitBtn.visibility = INVISIBLE
                }
            }
        }


        binding.submitBtn.setOnClickListener {

            if(getSelectedUserType() == null) return@setOnClickListener
            if(!validateDetails()) return@setOnClickListener
            if(getGender() == null) return@setOnClickListener

            countryCode = countryNameMap[binding.countryAutoCompleteTextView.text.toString()]!!
            districtId = districtNameMap[binding.districtAutoCompleteTextView.text.toString()]!!


            val userUpdateRequest = UserUpdateRequest(
                binding.institute.text.toString().trim(),
                districtId,
                binding.pinCode.text.toString().trim().toInt(),
                countryCode!!,
                binding.classorexam.text.toString().trim(),
                binding.email.text.toString().trim(),
                binding.dob.text.toString().trim(),
                profileImageBase64!!,
                binding.details.text.toString().trim(),
                getGender()!!,
                binding.qualification.text.toString().trim(),
                binding.specialization.text.toString().trim(),
                binding.phoneNo.text.toString().trim(),
                getSelectedUserType()!!,
                binding.userName.text.toString().trim(),
                binding.preflanguage.text.toString().trim()
            )

            Log.d(TAG, userUpdateRequest.toString())

            profileViewModel.updateUserDetails(userUpdateRequest)
        }

    }

    private fun validateDetails(): Boolean {
        if (TextUtils.isEmpty(binding.userName.text) ||
            TextUtils.isEmpty(binding.email.text) ||
            TextUtils.isEmpty(binding.phoneNo.text) ||
            TextUtils.isEmpty(binding.countryAutoCompleteTextView.text) ||
            TextUtils.isEmpty(binding.districtAutoCompleteTextView.text) ||
            TextUtils.isEmpty(binding.pinCode.text) ||
            TextUtils.isEmpty(binding.dob.text) ||
            TextUtils.isEmpty(binding.preflanguage.text) ||
            TextUtils.isEmpty(binding.details.text)
        ) {
            Toast.makeText(this, "Enter all field", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()) {
            Toast.makeText(this, "Please enter email in correct pattern", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        else if (!districtNameMap.containsKey(
                binding.districtAutoCompleteTextView.text.toString().trim())
        ) {
            Toast.makeText(this, "Select District from Suggestion", Toast.LENGTH_SHORT).show()
            return false
        } else if (!countryNameMap.containsKey(
                binding.countryAutoCompleteTextView.text.toString().trim())
        ) {
            Toast.makeText(this, "Select Country from Suggestion", Toast.LENGTH_SHORT).show()
            return false
        } else if (profileImageBase64 == null) {
            Toast.makeText(this, "Select / Capture Profile Image", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (getSelectedUserType() == "S") {
            if (TextUtils.isEmpty(binding.classorexam.text)) {
                Toast.makeText(this, "Enter Class/Exam", Toast.LENGTH_SHORT).show()
                return false
            }
            if (TextUtils.isEmpty(binding.institute.text)) {
                Toast.makeText(this, "Enter institute", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        else if (getSelectedUserType() == "A") {
            if (TextUtils.isEmpty(binding.specialization.text)) {
                Toast.makeText(this, "Enter Specialization", Toast.LENGTH_SHORT).show()
                return false
            }
            if (TextUtils.isEmpty(binding.qualification.text)) {
                Toast.makeText(this, "Enter qualification", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true

    }

    private fun getBase64Image(imageUri: Uri) {

        val imageInputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(imageInputStream)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()

        profileImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // Log.d(TAG, profileImageBase64.toString())


        /*val cursor = contentResolver.query(imageUri, null, null, null, null)
        if(cursor != null){
            val fileIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.moveToFirst()
            profileImageFile = File(cursor.getString(fileIndex))
            Log.d(TAG, profileImageFile.toString())
            cursor.close()
        }
*/
    }


    private fun getGender(): String? {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.maleBtn -> {
                "M"
            }

            R.id.femaleBtn -> {
                "F"
            }

            else -> {
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
                return null
            }
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

            else -> {
                "A"
            }
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
            binding.imageCard.visibility = GONE
            profileImageBase64 = null
            binding.imageCardImage.setImageURI(null)
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
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


            captureImageUri?.let { it1 ->
                getBase64Image(it1)
                binding.imageCardImage.setImageURI(it1)
                //profileImageUri = it1
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
                // profileImageUri = galleryImageUri
                getBase64Image(galleryImageUri)
                binding.imageCardImage.setImageURI(galleryImageUri)
                binding.imageCard.visibility = View.VISIBLE
                binding.imageCard.setOnClickListener { 
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

    private fun setDOB() {
        binding.dob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    var month1 = month
                    val dob = String.format(
                        Locale.ENGLISH,
                        "%4d-%02d-%02d",
                        year,
                        ++month1,
                        dayOfMonth
                    )
                    binding.dob.setText(dob)
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.show()
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
        if (sharedPreference.getDetail(USERTYPE, "String") == "S") {
            binding.userTypeSpinner.setSelection(1, true)
            binding.specialization.visibility = GONE
            binding.qualification.visibility = GONE
            binding.specialization.text = null
            binding.qualification.text = null
        } else {
            binding.userTypeSpinner.setSelection(2, true)
            binding.classorexam.visibility = GONE
            binding.institute.visibility = GONE
            binding.classorexam.text = null
            binding.institute.text = null
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