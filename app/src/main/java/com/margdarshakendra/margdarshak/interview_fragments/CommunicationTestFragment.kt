package com.margdarshakendra.margdarshak.interview_fragments

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.StudentHomeFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.databinding.FragmentCommunicationTestBinding
import com.margdarshakendra.margdarshak.models.UpdateCommunicationTimerRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.CommunicationTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.pow


@AndroidEntryPoint
class CommunicationTestFragment : Fragment() {

    private var isRecordingManuallyStopped = false
    private lateinit var binding: FragmentCommunicationTestBinding
    private val communicationTestViewModel by viewModels<CommunicationTestViewModel>()
    private lateinit var videoSelectLauncher: ActivityResultLauncher<String>
    private lateinit var countDownTimer: CountDownTimer
    private var hireTestId = 0
    private var selectedVideoUri: Uri? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var requiredPermissions: Array<String>
        private lateinit var processCameraFuture: ListenableFuture<ProcessCameraProvider>
    private var processCameraProvider: ProcessCameraProvider? = null
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    private var isVideoSelectedNotCaptured = false

    @Inject
    lateinit var sharedPreference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunicationTestBinding.inflate(inflater, container, false)

        requiredPermissions = mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        requestPermsAndGetHireTestId(requiredPermissions)

        selectVideoFile()

        binding.selectVideoBtn.setOnClickListener {
            videoSelectLauncher.launch("video/mp4")
        }

        binding.stopRecordingBtn.setOnClickListener {
            isRecordingManuallyStopped = true
            binding.selectVideoBtn.visibility = View.VISIBLE
            recording?.stop()
            binding.warningText.visibility = View.GONE
            Log.d(TAG, "Recording Stopped")
        }

        binding.clear.setOnClickListener {

            binding.videoCard.visibility = View.GONE
            selectedVideoUri = null
            binding.selectVideoBtn.visibility = View.VISIBLE
            binding.recordingTime.visibility = View.GONE
            binding.preview.visibility = View.GONE
            binding.videoView.visibility = View.GONE
            binding.stopRecordingBtn.visibility = View.GONE
            binding.cameraCard.visibility = View.GONE

        }

        submitTestOnClick()

        bindObservers()

    }

    private fun getHireTestId(){
        communicationTestViewModel.getHireTestId()
    }

    private fun requestPermsAndGetHireTestId(requiredPermissions : Array<String>){
        val permissionsToRequest = mutableListOf<String>()
        for (permission in requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
        else{
            getHireTestId()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ map ->
        val allPermissionGranted = map.values.all { it }
        if (allPermissionGranted) {
            getHireTestId()
        }
        else{
            binding.communicationQuestion.text = "First allow these permissions\n${requiredPermissions.toSet()} from App Info/ Setting\nthen restart the test the drawer"
            val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.contentText = "Open App Info to Allow Permissions"
            sweetAlertDialog.confirmText = "OK"
            sweetAlertDialog.show()
            sweetAlertDialog.setOnDismissListener {
                openAppInfo()
            }
            binding.timerText.visibility = View.GONE
            binding.timerText.visibility = View.GONE
            binding.answer.visibility = View.GONE
            binding.cameraCard.visibility = View.GONE
            binding.selectVideoBtn.visibility = View.GONE
            binding.submitBtn.visibility = View.GONE
        }
    }

    private fun openAppInfo() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        //intent.addCategory(Intent.CATEGORY_DEFAULT)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.recordingTime.visibility = View.VISIBLE
        binding.videoView.visibility= View.GONE
        binding.stopRecordingBtn.visibility= View.VISIBLE
        binding.preview.visibility= View.VISIBLE

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
        }

        // create and start a new recording session
        val videoName = SimpleDateFormat("ddMMyy_hhmmss", Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/margdarshak_video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(requireContext().applicationContext.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        Log.d(TAG, mediaStoreOutputOptions.collectionUri.toString())
        recording = videoCapture.output
            .prepareRecording(requireContext(), mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(requireContext(),
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.recordingTime.base = SystemClock.elapsedRealtime()
                        binding.recordingTime.start()
                        Log.d(TAG, "Recording Started !")
                    }
                    is VideoRecordEvent.Finalize -> {

                        binding.recordingTime.stop()

                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            if(isRecordingManuallyStopped){
                                selectedVideoUri = recordEvent.outputResults.outputUri
                                getVideoInfo(requireContext().applicationContext.contentResolver, selectedVideoUri!!)
                                setUpVideoView(recordEvent.outputResults.outputUri)
                            }
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.cause}")
                            val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog.contentText = "Video capture ends with error: " + "${recordEvent.cause}"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            val home =
                                if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {
                                    StudentHomeFragment()
                                } else {
                                    HomeFragment()
                                }
                            sweetAlertDialog.setOnDismissListener {
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.bReplace, home)
                                    .commit()
                                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(
                                    R.id.home
                                ).isChecked = true
                            }

                        }
                    }
                }
            }
    }

    private fun submitTestOnClick(){
        binding.submitBtn.setOnClickListener {

            val videoFilePart = validateSubmitRequestBodyAndReturnVideoFilePart() ?: return@setOnClickListener

            val answerBody = binding.answer.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val hireTestIdBody = hireTestId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            Log.d(TAG, "${videoFilePart.headers},\n ${hireTestIdBody.contentType()},\n $answerBody")


            Log.d(TAG, "submit Btn clicked")
            communicationTestViewModel.submitCommunicationTest(
                hireTestIdBody,
                answerBody,
                videoFilePart
            )
        }
    }

    private fun setUpVideoView(videoUri: Uri){

        binding.cameraCard.visibility = View.VISIBLE
        binding.videoView.visibility = View.VISIBLE
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.requestFocus()
        binding.videoView.setOnPreparedListener {
            binding.videoView.start()
        }
        binding.stopRecordingBtn.visibility = View.GONE
        if (isRecordingManuallyStopped && ! isVideoSelectedNotCaptured)
            binding.recordingTime.visibility = View.VISIBLE
        else binding.recordingTime.visibility = View.GONE
        binding.preview.visibility = View.GONE
        processCameraProvider?.unbindAll()
    }

    private fun startCameraX() {

        binding.videoView.visibility = View.GONE
        binding.preview.visibility = View.VISIBLE
        processCameraFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        preview = Preview.Builder().build()
        preview.setSurfaceProvider(cameraExecutor, binding.preview.surfaceProvider)

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.LOWEST))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)


        processCameraFuture.addListener({
            try {
                processCameraProvider = processCameraFuture.get()
                processCameraProvider?.unbindAll()
                processCameraProvider?.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, videoCapture)
            } catch (e: Exception) {
                Log.d(TAG, "${e.message}")
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
            }


        }, ContextCompat.getMainExecutor(requireContext()))


    }

    private fun selectVideoFile() {

        videoSelectLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
                result?.let {
                    selectedVideoUri = result
                    isVideoSelectedNotCaptured = true

                    Log.d(TAG, selectedVideoUri.toString())
                    getVideoInfo(requireContext().applicationContext.contentResolver, selectedVideoUri!!)
                    setUpVideoView(selectedVideoUri!!)

                }
            }
    }

    private fun validateSubmitRequestBodyAndReturnVideoFilePart(): MultipartBody.Part? {
        if(hireTestId == 0){
            Toast.makeText(requireContext(), "Test is not started yet", Toast.LENGTH_SHORT).show()
            return null
        }
        if (selectedVideoUri == null) {
            Toast.makeText(
                requireContext(),
                "Please Select Video",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        if (binding.answer.visibility == View.VISIBLE && binding.answer.text.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please Write Some Answer",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        val videoFilePart = getSelectedOrCapturedVideoMultiPart(requireContext().applicationContext.contentResolver, selectedVideoUri!!)
        if(videoFilePart == null) {
            Toast.makeText(requireContext(), "Video File is null", Toast.LENGTH_SHORT).show()
            return null
        }
        return videoFilePart
    }

    private fun createLocalFileFromVideoUri(contentResolver: ContentResolver, videoName: String, videoUri: Uri): File? {
        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(videoUri, "r") ?: return null

        // Create a local file with the video name
        val videoFile = File(requireContext().applicationContext.filesDir, videoName)
        // Open an InputStream for the selected video Uri
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)

        if (!videoFile.createNewFile()) {
            Log.d(TAG, "File Already Exists")
        } else Log.d(TAG, "Video File created !")
        videoFile.outputStream().use {
            inputStream.copyTo(it)
            it.close()
        }
        inputStream.close()
        parcelFileDescriptor.close()
        return videoFile
    }


    /*


    private fun createLocalFileFromVideoUri(): File? {
        try {
            val contentResolver: ContentResolver =
                requireContext().applicationContext.contentResolver

            // Get the file name from the MediaStore
            val videoName = getVideoNameFromUri(contentResolver, videoUri)

            // Create a local file with the video name
            val localFile = File(requireContext().applicationContext.filesDir, videoName)
            // Open an InputStream for the selected video Uri
            val inputStream = contentResolver.openInputStream(videoUri)

            localFile.createNewFile()
            localFile.outputStream().use {
                inputStream?.copyTo(it)
            }

            // Close the streams
            //inputStream?.close()
            // File created successfully
            return localFile
        } catch (e: IOException) {
            // Handle IOException, e.g., log an error message
            e.printStackTrace()
            return null
        }
    }

      private fun copyFileToPath(sourceFilePath: String, destinationDirectory: String, destinationFileName: String): File? {
          try {
              // Create a File object for the source file
              val sourceFile = File(sourceFilePath)

              // Create a File object for the destination directory and file
              val destinationDir = File(destinationDirectory)
              val destinationFile = File(destinationDir, destinationFileName)

              // Ensure the destination directory exists; create it if needed
              if (!destinationDir.exists()) {
                  destinationDir.mkdirs()
              }

              // Create FileChannels for input and output
              val sourceChannel = FileInputStream(sourceFile).channel
              val destinationChannel = FileOutputStream(destinationFile).channel

              // Transfer the content from the source channel to the destination channel
              destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size())

              // Close the channels
              sourceChannel.close()
              destinationChannel.close()

              return destinationFile

              // File copied successfully
          } catch (e: IOException) {
              // Handle IOException, e.g., log an error message
              e.printStackTrace()
          }
          return null
      }

      private fun createVideoOutputPath(context: Context): String {
          val contentResolver = context.contentResolver

     Represent the videos collection
        val videosCollection: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){ // if sdk is 29 or higher
           videosCollection =  MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }
        else videosCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

     Represents the data values of the video to be saved
        val contentValues = ContentValues()

        // Adding the file title to the content values
        contentValues.put(
            MediaStore.Video.Media.TITLE,
            "VID_" + System.currentTimeMillis() + ".mp4"
        )
        // Adding the file display name to the content values
        contentValues.put(
            MediaStore.Video.Media.DISPLAY_NAME,
            "VID_" + System.currentTimeMillis() + ".mp4"
        )
     Represents the uri of the inserted video
        val videoUri = contentResolver.insert(videosCollection, contentValues)!!
        // Opening a stream on to the content associated with the video content uri
        contentResolver.openOutputStream(videoUri)
     Represents the file path of the video uri
        val outputPath = getUriRealPath(requireContext().contentResolver, videoUri)
        // Deleting the video uri to create it later with the actual video
        contentResolver.delete(videoUri, null, null)
        return outputPath
    }

    private fun getUriRealPath(contentResolver: ContentResolver, uri: Uri): String {
        var filePath = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                var columnName = MediaStore.Images.Media.DATA
                when (uri) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> {
                        columnName = MediaStore.Images.Media.DATA
                    }
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> {
                        columnName = MediaStore.Video.Media.DATA
                    }
                }
                val filePathColumnIndex = cursor.getColumnIndex(columnName)
                filePath = cursor.getString(filePathColumnIndex)
            }
            cursor.close()
        }
        return filePath
    }
      private fun createFileFromParcelFileDescriptor(uri: Uri): File? {
          val contentResolver = requireContext().contentResolver

          try {
              val parcelFileDescriptor: ParcelFileDescriptor? = contentResolver.openFileDescriptor(uri, "r")

              if (parcelFileDescriptor != null) {
                  val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor

                  // Create a temporary file to store the content
                  val tempFile = File(requireContext().applicationContext.cacheDir, "temp_video.mp4")
                  val tempFileOutputStream = FileOutputStream(tempFile)

                  // Use InputStream to read the content from the file descriptor
                  val inputStream = FileInputStream(fileDescriptor)
                  val buffer = ByteArray(1024)
                  var bytesRead: Int

                  while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                      tempFileOutputStream.write(buffer, 0, bytesRead)
                  }

                  // Close the streams and descriptors
                  inputStream.close()
                  tempFileOutputStream.close()
                  parcelFileDescriptor.close()

                  return tempFile

                  // Now you have a File object representing the created file (outputFile)
              } else {
                  // Handle the case where the ParcelFileDescriptor is null
              }
          } catch (e: Exception) {
              // Handle exceptions, such as IOException or SecurityException
              e.printStackTrace()
          }
          return null
      }

        private fun createFile(videoUri: Uri) {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                requireContext().contentResolver.openFileDescriptor(uri, "r")

            if (parcelFileDescriptor != null) {
                val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor

                // Create a RequestBody from the file descriptor
                val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull())

            }
        }

*/


    private fun getSelectedOrCapturedVideoMultiPart(contentResolver: ContentResolver, videoUri: Uri): MultipartBody.Part? {

        val videoName =
            binding.docTitle.text.toString() //"\"MargVideo_${dateFormat.format(System.currentTimeMillis())}.mp4\""

        val videoFile =
            createLocalFileFromVideoUri(contentResolver, videoName, videoUri)
                ?: return null
        Log.d(TAG, videoFile.toString())

        val videoFileRequestBody =
            videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
        val videoFilePart = MultipartBody.Part.createFormData(
            "fileupload",
            videoFile.name,
            videoFileRequestBody
        )
        return videoFilePart
    }

    private fun getVideoInfo(contentResolver: ContentResolver, videoUri: Uri) {
        Log.d(TAG, "Video Info Called")
        val videoCursor = contentResolver.query(videoUri, null, null, null, null)

        videoCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                // Get video name
                val videoName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                Log.d(TAG, "Video Name: $videoName")
                binding.docTitle.text = videoName

                // Get video size
                var videoSize =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                Log.d(TAG, "Video Size: $videoSize bytes")

                if (videoSize < 900000) {
                    videoSize /= 10.0.pow(3.0)
                    val docSize = "Size : " + String.format("%.2f", videoSize) + " KB"
                    binding.docSize.text = docSize
                } else {
                    videoSize /= 10.0.pow(6.0)
                    val docSize = "Size : " + String.format("%.2f", videoSize) + " MB"
                    binding.docSize.text = docSize
                }

                binding.selectVideoBtn.visibility = View.GONE
                binding.videoCard.visibility = View.VISIBLE

                cursor.close()

                /*// Get video thumbnail (for simplicity, you may want to load it into an ImageView)
               val videoThumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                   contentResolver.loadThumbnail(
                       videoUri,
                       Size(30, 30),
                       null
                   )
               } else {
                   MediaStore.Video.Thumbnails.getThumbnail(
                       contentResolver,
                       videoUri.lastPathSegment!!.toLong(),
                       MediaStore.Video.Thumbnails.MICRO_KIND,
                       null
                   )
               }

               binding.docImage.setImageBitmap(videoThumbnail)*/

                // Use videoThumbnail as needed
            }
        }
    }

    private fun bindObservers() {

        val skeleton = binding.root.createSkeleton()
        communicationTestViewModel.hireTestIdLiveData.observe(viewLifecycleOwner) {
            skeleton.showOriginal()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    if (it.data.test_data == null) {
                        val sweetAlertDialog =
                            SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                        sweetAlertDialog.contentText = "You have Submitted The Test"
                        sweetAlertDialog.confirmText = "OK"
                        sweetAlertDialog.show()
                        val home =
                            if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {
                                StudentHomeFragment()
                            } else {
                                HomeFragment()
                            }
                        sweetAlertDialog.setOnDismissListener {
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.bReplace, home)
                                .commit()
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(
                                R.id.home
                            ).isChecked = true
                        }
                        return@observe
                    }

                    startCameraX()
                    captureVideo()

                    it.data.test_data.testime?.toInt()?.let { it1 -> startTimer(it1) }

                    binding.communicationQuestion.text = HtmlCompat.fromHtml(
                        it.data.test_data.testopic,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()

                    hireTestId = it.data.test_data.hiretestID

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    skeleton.showSkeleton()
                }
            }
        }

        communicationTestViewModel.updateCommunicationTimerLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    // Log.d(TAG, it.data!!.toString())

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        val sweetProgressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        sweetProgressDialog.setCanceledOnTouchOutside(false)
        sweetProgressDialog.contentText = "Please Wait..."
        communicationTestViewModel.submitCommunicationTestLiveData.observe(viewLifecycleOwner) {
            sweetProgressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    val sweetAlertDialog =
                        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    sweetAlertDialog.contentText = it.data.message
                    sweetAlertDialog.confirmText = "OK"
                    sweetAlertDialog.show()
                    val home =
                        if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {
                            StudentHomeFragment()
                        } else {
                            HomeFragment()
                        }
                    sweetAlertDialog.setOnDismissListener {
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
                    sweetProgressDialog.show()
                }
            }
        }

    }

    private fun updateTimer(updateCommunicationTimerRequest: UpdateCommunicationTimerRequest) {
        communicationTestViewModel.updateCommunicationTestTimer(updateCommunicationTimerRequest)
    }

    private fun startTimer(totalTestDurationInMinutes: Int) {

        // Set the duration of the timer in milliseconds
        val durationInMillis: Long = TimeUnit.MINUTES.toMillis(totalTestDurationInMinutes.toLong())
        // Set the interval of the timer in milliseconds (e.g., 1 second)
        val intervalInMillis: Long = 1000
        countDownTimer = object : CountDownTimer(durationInMillis, intervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                val totalLeftSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toInt()
                updateTimer(
                    UpdateCommunicationTimerRequest(
                        hiretestID = 0,
                        totalLeftSeconds
                    )
                )
                // Convert milliseconds to minutes and seconds
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(minutes)

                // Update the UI on each tick
                val timeLeftText =
                    if (totalLeftSeconds <= 10) "Test Will be end in $totalLeftSeconds second, But You have to submit Test Manually"
                    else "Time Left - ${String.format(" %02d: %02d", minutes, seconds)}"
                binding.timerText.text = timeLeftText

            }

            override fun onFinish() {
                binding.timerText.setTextColor(requireContext().getColor(R.color.green))
                binding.timerText.text = "Timer Finished, Now Submit the Test"
                binding.answer.text = null
                binding.answer.visibility = View.GONE
            }

        }
        countDownTimer.start()

    }

    private fun saveVideoRecording(){
        Log.d(TAG, recording.toString())
        if( ! isRecordingManuallyStopped && recording != null){
            recording?.stop()
            Snackbar.make(requireContext(), binding.root, "Recording Stopped And Saved !", Snackbar.LENGTH_LONG).show()
            Log.d(TAG, "Recording Paused")
        }
    }

    private fun restartVideoRecording() {
        if (! isRecordingManuallyStopped && !isVideoSelectedNotCaptured && recording != null) {
            binding.videoCard.visibility = View.GONE
            selectedVideoUri = null
            startCameraX()
            captureVideo()
            Snackbar.make(
                requireContext(),
                binding.root,
                "Recording Restarted !",
                Snackbar.LENGTH_LONG
            ).show()
            Log.d(TAG, "Recording Restarted")
        }
    }

    override fun onPause() {
        saveVideoRecording()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        restartVideoRecording()
    }

    override fun onDestroy() {
        super.onDestroy()
        processCameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }
}