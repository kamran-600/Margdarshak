package com.margdarshakendra.margdarshak.interview_fragments

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.StudentHomeFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.databinding.FragmentDocsUploadBinding
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.DocsUploadViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import kotlin.math.pow

@AndroidEntryPoint
class DocsUploadFragment : Fragment() {
    private lateinit var binding: FragmentDocsUploadBinding

    private var selectedPhotoIdUri : Uri?= null
    private var selectedPanCardUri : Uri?= null
    private var selectedHACUri : Uri?= null
    private var selectedPCUri : Uri?= null
    private var selectedLACUri : Uri?= null

    private val docsUploadViewModel by viewModels<DocsUploadViewModel>()

    @Inject
    lateinit var sharedPreference:SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocsUploadBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handlePhotoId()
        handlePanCard()
        handleHAC()
        handlePC()
        handleLAC()

        binding.submitBtn.setOnClickListener {
            if(selectedPhotoIdUri== null) {
                val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                sweetAlertDialog.contentText = "Please Select Photo Id"
                sweetAlertDialog.confirmText = "OK"
                sweetAlertDialog.show()
                return@setOnClickListener
            }

            val id = getFileMultiPart(requireContext().contentResolver, selectedPhotoIdUri, "id")
            val pan =  getFileMultiPart(requireContext().contentResolver, selectedPanCardUri, "pan")
            val hac = getFileMultiPart(requireContext().contentResolver, selectedHACUri, "hac")
            val pc = getFileMultiPart(requireContext().contentResolver, selectedPCUri, "pc")
            val lac = getFileMultiPart(requireContext().contentResolver, selectedLACUri, "lac")

            docsUploadViewModel.submitDocsUpload(id!!,pan, hac, pc, lac)

        }


        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.contentText = "Please Wait..."
        sweetAlertDialog.setCanceledOnTouchOutside(false)
        docsUploadViewModel.submitDocsUploadLiveData.observe(viewLifecycleOwner){
            sweetAlertDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                     Log.d(TAG, it.data!!.toString())
                    val sweetSuccessDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    sweetSuccessDialog.contentText = it.data.message
                    sweetSuccessDialog.confirmText = "OK"
                    sweetSuccessDialog.show()
                    val home =
                        if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {
                            StudentHomeFragment()
                        } else {
                            HomeFragment()
                        }
                    sweetSuccessDialog.setOnDismissListener {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.bReplace, home)
                            .commit()
                        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(
                            R.id.home
                        ).isChecked = true
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    sweetAlertDialog.show()
                }
            }
        }

    }

    private fun handlePhotoId(){
        val photoIdLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result?.data?.let {
                selectedPhotoIdUri = result.data!!.data
                getFileDetails(selectedPhotoIdUri!!, binding.photoIdCard, binding.photoIdTitle, binding.photoIdImage, binding.photoIdType, binding.photoIdSize)
            }
        }
        binding.choosePhotoId.setOnClickListener {
            photoIdLauncher.launch(getGalleryIntent())
        }
        binding.clearPhotoId.setOnClickListener {
            binding.photoIdCard.visibility = View.GONE
            selectedPhotoIdUri = null
        }
    }

    private fun handlePanCard(){
        val panCardLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result?.data?.let {
                selectedPanCardUri = result.data!!.data
                getFileDetails(selectedPanCardUri!!, binding.panCard, binding.panCardTitle, binding.panCardImage, binding.panCardType, binding.panCardSize)
            }
        }
        binding.choosePanCard.setOnClickListener {
            panCardLauncher.launch(getGalleryIntent())
        }
        binding.clearPanCard.setOnClickListener {
            binding.panCard.visibility = View.GONE
            selectedPanCardUri = null
        }
    }

    private fun handleHAC(){
        val hacLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result?.data?.let {
                selectedHACUri = result.data!!.data
                getFileDetails(selectedHACUri!!, binding.hacCard, binding.hacTitle, binding.hacImage, binding.hacType, binding.hacSize)

            }
        }
        binding.chooseHAC.setOnClickListener {
            hacLauncher.launch(getGalleryIntent())
        }
        binding.clearHAC.setOnClickListener {
            binding.hacCard.visibility = View.GONE
            selectedHACUri = null
        }

    }

    private fun handlePC(){
        val pcLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result?.data?.let {
                selectedPCUri = result.data!!.data
                getFileDetails(selectedPCUri!!, binding.pcCard, binding.pcTitle, binding.pcImage, binding.pcType, binding.pcSize)
            }
        }
        binding.choosePC.setOnClickListener {

            pcLauncher.launch(getGalleryIntent())
        }
        binding.clearPC.setOnClickListener {
            binding.pcCard.visibility = View.GONE
            selectedPCUri = null
        }
    }

    private fun handleLAC(){
        val lacLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            result?.data?.let {
                selectedLACUri = result.data!!.data
                getFileDetails(selectedLACUri!!, binding.lacCard, binding.lacTitle, binding.lacImage, binding.lacType, binding.lacSize)
            }
        }
        binding.chooseLAC.setOnClickListener {
            lacLauncher.launch(getGalleryIntent())
        }
        binding.clearLAC.setOnClickListener {
            binding.lacCard.visibility = View.GONE
            selectedLACUri = null
        }
    }


    private fun getGalleryIntent(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf("image/jpeg","image/jpg", "image/png", "application/pdf")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.type = mimeTypes.joinToString(",")
        return intent
    }

    private fun getFileDetails(fileUri: Uri, card: MaterialCardView, titleView: MaterialTextView, imageView: ShapeableImageView, fileTypeView: MaterialTextView, fileSizeView: MaterialTextView){
        val cursor = requireContext().contentResolver.query(fileUri, null, null, null, null)
        val fileType = requireContext().contentResolver.getType(fileUri)
        Log.d(TAG, fileType.toString())
        val validFileType = listOf("image/jpeg","image/jpg","image/png","application/pdf" )
        if(validFileType.contains(fileType)){
            cursor?.use {
                it.moveToFirst()
                val nameIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                titleView.text = it.getString(nameIndex)
                val sizeIndex = it.getColumnIndexOrThrow(OpenableColumns.SIZE)
                var i = it.getDouble(sizeIndex)
                if (i < 900000) {
                    i /= 10.0.pow(3.0)
                    fileSizeView.text = "Size : " + String.format("%.2f", i) + " KB"
                } else {
                    i /= 10.0.pow(6.0)
                    fileSizeView.text = "Size : " + String.format("%.2f", i) + " MB"
                }
                fileTypeView.text = fileType
                card.visibility = View.VISIBLE
            }
        }
        else {
            Toast.makeText(requireContext(),"Please Select Only JPEG, JPG, PNG, PDF", Toast.LENGTH_SHORT).show()
            return
        }
    }


    private fun createLocalFileFromUri(contentResolver: ContentResolver, fileUri: Uri?): File? {
        if(fileUri == null) return null
        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(fileUri, "r") ?: return null

        val fileName = getFileName(fileUri)?: return null
        // Create a local file with the video name
        val file = File(requireContext().applicationContext.filesDir, fileName)
        // Open an InputStream for the selected video Uri
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

        if (!file.createNewFile()) {
            Log.d(TAG, "File Already Exists")
        } else Log.d(TAG, "Video File created !")
        file.outputStream().use {
            inputStream.copyTo(it)
            it.close()
        }
        inputStream.close()
        parcelFileDescriptor.close()
        return file
    }


    private fun getFileName(fileUri: Uri): String? {
        val cursor = requireContext().contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val fileName = it.getString(nameIndex)
            it.close()
            return fileName
        }
        return null
    }

    private fun getFileMultiPart(contentResolver: ContentResolver, fileUri: Uri?, key: String): MultipartBody.Part? {

        val videoFile =
            createLocalFileFromUri(contentResolver, fileUri)
                ?: return null
        Log.d(TAG, fileUri.toString())
        Log.d(TAG, videoFile.toString())

        val fileType = contentResolver.getType(fileUri!!)

        val videoFileRequestBody =
            videoFile.asRequestBody(fileType?.toMediaTypeOrNull())
        val videoFilePart = MultipartBody.Part.createFormData(
            key,
            videoFile.name,
            videoFileRequestBody
        )
        return videoFilePart
    }




}