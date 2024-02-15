package com.margdarshakendra.margdarshak.dashboard_bottom_fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.faltenreich.skeletonlayout.SkeletonConfig
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.margdarshakendra.margdarshak.DashboardActivity
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.adapters.CounsellingDataRecAdapter
import com.margdarshakendra.margdarshak.adapters.PagingLoadingAdapter
import com.margdarshakendra.margdarshak.broadcastReceivers.NotificationReceiver
import com.margdarshakendra.margdarshak.databinding.FragmentCounsellingBinding
import com.margdarshakendra.margdarshak.databinding.InductionLayoutBinding
import com.margdarshakendra.margdarshak.databinding.MakeCallLayoutBinding
import com.margdarshakendra.margdarshak.databinding.SendEmailLayoutBinding
import com.margdarshakendra.margdarshak.databinding.SendSmsLayoutBinding
import com.margdarshakendra.margdarshak.databinding.ShortlistForHiringDialogLayoutBinding
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.InductionRequest
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.ShortListUserRequest
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.HtmlImageGetter
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.CounsellingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CounsellingFragment : Fragment() {

    private lateinit var binding: FragmentCounsellingBinding

    private val counsellingViewModel by viewModels<CounsellingViewModel>()


    private lateinit var materialAlertDialog: AlertDialog
    private var searchedEmailUserID: String? = null
    private var selectedPositionInductionBy: Int? = null


    lateinit var sendSmsLayoutBinding: SendSmsLayoutBinding
    lateinit var sendEmailLayoutBinding: SendEmailLayoutBinding
    private lateinit var makeCallLayoutBinding: MakeCallLayoutBinding


    private var templateId = 0
    private var templateHtmlText = ""
    private var employerId = 0
    private var employerPostId = 0
    private var uid = 0

    private lateinit var templateMap: LinkedHashMap<String, Int>
    private lateinit var statusMap: LinkedHashMap<String, String>
    private lateinit var employerMap: LinkedHashMap<String, Int>

    private var whatsappClicked = false
    private var smsClicked = false
    private var emailClicked = false
    private var callClicked = false

    @Inject
    lateinit var sharedPreference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCounsellingBinding.inflate(inflater, container, false)

        templateMap = LinkedHashMap()
        statusMap = LinkedHashMap()

        sendSmsLayoutBinding = SendSmsLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        sendEmailLayoutBinding =
            SendEmailLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        makeCallLayoutBinding = MakeCallLayoutBinding.inflate(LayoutInflater.from(requireContext()))


        //getCounsellingData()



        val counsellingDataRecAdapter = CounsellingDataRecAdapter(
            requireContext(),
            ::makeCall,
            ::sendCloudMessage,
            ::sendWhatsappMessage,
            ::sendEmailMessage,
            ::showPopMenu
        )


        getCounsellingData()  // it is called for only get employer data

        val skeleton = binding.counsellingDataRecyclerView.applySkeleton(
            R.layout.single_row_data, 5, SkeletonConfig(
                maskColor = ContextCompat.getColor(requireContext(), SkeletonLayout.DEFAULT_MASK_COLOR),
                maskCornerRadius = 150f,
                showShimmer = SkeletonLayout.DEFAULT_SHIMMER_SHOW,
                shimmerColor = ContextCompat.getColor(requireContext(), SkeletonLayout.DEFAULT_SHIMMER_COLOR),
                shimmerDurationInMillis = SkeletonLayout.DEFAULT_SHIMMER_DURATION_IN_MILLIS,
                shimmerDirection = SkeletonLayout.DEFAULT_SHIMMER_DIRECTION,
                shimmerAngle = SkeletonLayout.DEFAULT_SHIMMER_ANGLE
            )
        )

        binding.swipeRefresh.setOnRefreshListener {
            // getCounsellingData()
            counsellingDataRecAdapter.refresh()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                counsellingViewModel.counsellingPagingFlow.collect{
                    counsellingDataRecAdapter.submitData(it)
                }
            }
        }

        counsellingDataRecAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                   //skeleton.showSkeleton()
                    Log.d(TAG, "screen data loading")
                }

                is LoadState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    val error =
                        "Error: ${(loadState.refresh as LoadState.Error).error.localizedMessage}"
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, error)
                }

                is LoadState.NotLoading -> {
                    binding.swipeRefresh.isRefreshing = false
                   // skeleton.showOriginal()
                    Log.d(TAG, "screen data received")
                    if(binding.counsellingDataRecyclerView.adapter == null){
                        binding.counsellingDataRecyclerView.setHasFixedSize(true)
                        binding.counsellingDataRecyclerView.adapter = counsellingDataRecAdapter.withLoadStateHeaderAndFooter(
                            header = PagingLoadingAdapter(),
                            footer = PagingLoadingAdapter()
                        )
                        Log.d(TAG, "adapter is null !")
                    }else Log.d(TAG, "adapter is not null !")

                }
            }
        }

        counsellingViewModel.counsellingDataResponseLiveData.observe(viewLifecycleOwner) {
            // skeleton.showOriginal()
            when (it) {
                is NetworkResult.Success -> {
                   // binding.swipeRefresh.isRefreshing = false
                    Log.d(TAG, it.data!!.toString())


                    employerMap = LinkedHashMap()

                    if (!it.data.employers.isNullOrEmpty()) {
                        for (i in it.data.employers) {
                            if (i != null) employerMap[i.name] = i.userID
                        }
                    }
                }

                is NetworkResult.Error -> {
                   // binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    // skeleton.showSkeleton()
                }
            }
        }



        val progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        progressDialog.contentText = "Please Wait..."
        progressDialog.setCanceledOnTouchOutside(false)
        counsellingViewModel.callResponseLiveData.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.data.toString())
                    Toast.makeText(requireContext(), it.data.data.message, Toast.LENGTH_SHORT)
                        .show()
                    showSuccessAndRefreshActivity(it.data.data.message)

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    progressDialog.show()
                }
            }
        }

        counsellingViewModel.crmResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.data.sms.toString())
                    setFollowDate()
                    setTime()
                    populateStatusSpinner(it.data.data.status)

                    if (smsClicked) {
                        Log.d(TAG, smsClicked.toString())
                        populateTemplateSpinnerSms(it.data.data.sms)
                    } else if (whatsappClicked) {
                        populateTemplateSpinnerWhatsApp(it.data.data.whatsapp)
                    } else if (emailClicked) {
                        populateTemplateSpinnerEmail(it.data.data.email)
                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        counsellingViewModel.smsResponseLiveData.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()

                    /*val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault())

                    var date = ""

                    if (callClicked) {
                        date =
                            "${makeCallLayoutBinding.followUpDate.text} ${makeCallLayoutBinding.time.text}"
                    } else {
                        date =
                            "${sendSmsLayoutBinding.followUpDate.text} ${sendSmsLayoutBinding.time.text}"
                    }

                    var convertedDate: Date? = null
                    try {
                        convertedDate = dateFormat.parse(date)
                    } catch (e: ParseException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }

                    if (convertedDate != null) {
                        Log.d(TAG, date)
                        Log.d(TAG, convertedDate.toString())
                        val calendar = Calendar.getInstance()
                        calendar.time = convertedDate
                        calendar.add(Calendar.MINUTE, -10)
                        val timeInMillis = calendar.timeInMillis
                        Log.d(TAG, calendar.time.toString())
                        val onlyTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        scheduleNotification(timeInMillis, onlyTimeFormat.format(calendar.time))

                    }*/
                    showSuccessAndRefreshActivity(it.data.message)
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    progressDialog.show()
                }
            }
        }

        counsellingViewModel.emailSmsResponseLiveData.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    // scheduleNotification(sendEmailLayoutBinding.followUpDate.text)

                    /* val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault())

                     val date =
                         "${sendEmailLayoutBinding.followUpDate.text} ${sendEmailLayoutBinding.time.text}"

                     var convertedDate: Date? = null
                     try {
                         convertedDate = dateFormat.parse(date)
                     } catch (e: ParseException) {
                         Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                     }

                     if (convertedDate != null) {
                         Log.d(TAG, date)
                         Log.d(TAG, convertedDate.toString())
                         val calendar = Calendar.getInstance()
                         calendar.time = convertedDate!!
                         calendar.add(Calendar.MINUTE, -10)
                         val timeInMillis = calendar.timeInMillis
                         Log.d(TAG, calendar.time.toString())

                         val onlyTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                         scheduleNotification(timeInMillis, onlyTimeFormat.format(calendar.time))
                         Log.d(TAG, "scheduled  notification")

                     }*/
                    showSuccessAndRefreshActivity(it.data.message)
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    progressDialog.show()
                }
            }
        }

        counsellingViewModel.templateResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    templateHtmlText = it.data.template
                    if (smsClicked) {
                        sendSmsLayoutBinding.message.setText(it.data.template)

                    } else if (whatsappClicked) {
                        sendSmsLayoutBinding.message.setText(it.data.template)
                        sendSmsLayoutBinding.variableList.setText(it.data.variables)
                    } else if (emailClicked) {
                        sendEmailLayoutBinding.subject.setText(it.data.subject)
                        sendEmailLayoutBinding.message.setText(
                            HtmlCompat.fromHtml(
                                it.data.template, HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                        )

                        val glide = Glide.with(requireContext())
                        val imageGetter = HtmlImageGetter(
                            lifecycleScope, resources, glide, sendEmailLayoutBinding.message
                        )
                        val styledText = HtmlCompat.fromHtml(
                            it.data.template, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter, null
                        )
                        sendEmailLayoutBinding.message.setText(styledText)

                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }


        return binding.root
    }


    private fun showSuccessAndRefreshActivity(contentText: String){
        val successDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
        successDialog.contentText = contentText
        successDialog.show()
        successDialog.setOnDismissListener {
            val intent = Intent(requireContext(), DashboardActivity::class.java)
            intent.putExtra("OpenFragment", "counselling" )
            requireActivity().startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requireActivity().window.setWindowAnimations(AppCompatActivity.OVERRIDE_TRANSITION_OPEN)
                requireActivity().overrideActivityTransition(AppCompatActivity.OVERRIDE_TRANSITION_OPEN,0,0)
            }
            else requireActivity().overridePendingTransition(0,0)
            requireActivity().finishAffinity()
            //counsellingDataRecAdapter.refresh()
        }
    }

    private fun showPopMenu(view: View, uid: Int, userName: String) {
        Log.d(TAG, "showPopMenu is clicked")
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.counselling_pop_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.shortListForHiring -> {

                    employerId = 0
                    employerPostId = 0
                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())

                    val shortlistForHiringDialogLayoutBinding =
                        ShortlistForHiringDialogLayoutBinding.inflate(
                            LayoutInflater.from(
                                requireContext()
                            )
                        )

                    dialogBuilder.setView(shortlistForHiringDialogLayoutBinding.root)
                    val materialAlertDialog = dialogBuilder.create()
                    materialAlertDialog.show()

                    materialAlertDialog.setCanceledOnTouchOutside(false)

                    shortlistForHiringDialogLayoutBinding.userName.text = userName
                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setAdapter(
                        null
                    )
                    shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setAdapter(
                        null
                    )

                    val employerListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        employerMap.keys.toList()
                    )
                    employerListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setAdapter(
                        employerListAdapter
                    )

                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        employerId =
                            employerMap[shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.text.toString()]!!
                        shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setAdapter(
                            null
                        )
                        shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.text =
                            null
                        counsellingViewModel.getFilterPosts(employerId)
                        Log.d(TAG, employerId.toString())
                    }
                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setOnClickListener {
                        shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.showDropDown()
                    }
                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.showDropDown()
                        }
                    }

                    counsellingViewModel.filteredPostsDataLiveData.observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is NetworkResult.Success -> {
                                Log.d(TAG, response.data!!.toString())

                                val employerPostMap = LinkedHashMap<String, Int>()

                                if (!response.data.data.isNullOrEmpty()) {
                                    for (i in response.data.data) {
                                        employerPostMap[i.position] = i.postID
                                    }
                                } else return@observe

                                val employerPostListAdapter = ArrayAdapter(
                                    requireContext(), android.R.layout.simple_spinner_dropdown_item,
                                    employerPostMap.keys.toList()
                                )
                                employerPostListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setAdapter(
                                    employerPostListAdapter
                                )

                                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                                    employerPostId =
                                        employerPostMap[shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.text.toString()]!!
                                    Log.d(TAG, employerPostId.toString())

                                }
                                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setOnClickListener {
                                    shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.showDropDown()
                                }
                                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                                    if (hasFocus) {
                                        shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.showDropDown()
                                    }
                                }
                            }

                            is NetworkResult.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d(TAG, response.message.toString())
                            }

                            is NetworkResult.Loading -> {

                            }
                        }
                    }

                    shortlistForHiringDialogLayoutBinding.cancelButton.setOnClickListener {
                        materialAlertDialog.dismiss()
                    }
                    shortlistForHiringDialogLayoutBinding.close.setOnClickListener {
                        materialAlertDialog.dismiss()
                    }
                    shortlistForHiringDialogLayoutBinding.submit.setOnClickListener {
                        if (employerId == 0) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            sweetAlertDialog.contentText = "Please Select Employer"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        if (employerPostId == 0) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            sweetAlertDialog.contentText = "Please Select Employer Position"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        val shortListUserRequest = ShortListUserRequest(
                            uid.toString(),
                            employerId.toString(),
                            employerPostId.toString()
                        )
                        counsellingViewModel.shortListUser(shortListUserRequest)
                        Log.d(TAG, shortListUserRequest.toString())

                        counsellingViewModel.shortListUserResponseLiveData.observe(
                            viewLifecycleOwner
                        ) { response ->
                            when (response) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, response.data!!.toString())
                                    Toast.makeText(
                                        requireContext(),
                                        response.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    materialAlertDialog.dismiss()
                                    if (counsellingViewModel.shortListUserResponseLiveData.hasObservers()) {
                                        counsellingViewModel.shortListUserResponseLiveData.removeObservers(
                                            viewLifecycleOwner
                                        )
                                    }

                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        response.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(TAG, response.message.toString())
                                    materialAlertDialog.dismiss()
                                    if (counsellingViewModel.shortListUserResponseLiveData.hasObservers()) {
                                        counsellingViewModel.shortListUserResponseLiveData.removeObservers(
                                            viewLifecycleOwner
                                        )
                                    }
                                    return@observe
                                }

                                is NetworkResult.Loading -> {

                                }
                            }
                        }
                    }

                }

                R.id.inductionOrPresentation -> {

                    /**Create Dialog*/

                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())

                    val inductionLayoutBinding =
                        InductionLayoutBinding.inflate(
                            LayoutInflater.from(
                                requireContext()
                            )
                        )

                    dialogBuilder.setView(inductionLayoutBinding.root)
                    val materialAlertDialog = dialogBuilder.create()
                    materialAlertDialog.show()

                    materialAlertDialog.setCanceledOnTouchOutside(false)

                    inductionLayoutBinding.userName.text = userName
                    inductionLayoutBinding.meetLink.setText(
                        sharedPreference.getDetail(
                            Constants.USERMEETLINK,
                            "String"
                        ) as String?
                    )

                    /**
                     * InductionBy AutoCompleteTextView
                     */

                    val inductionByList = listOf("Self", "Other")
                    val inductionListAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        inductionByList
                    )
                    inductionListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    inductionLayoutBinding.inductionByAutoCompleteTextView.setAdapter(
                        inductionListAdapter
                    )

                    inductionLayoutBinding.inductionByAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                        selectedPositionInductionBy = position
                        if (position == 0) {
                            inductionLayoutBinding.email.visibility = View.GONE
                            inductionLayoutBinding.searchedEmailUserName.visibility = View.GONE
                            searchedEmailUserID = null
                            inductionLayoutBinding.searchedEmailUserName.text = null
                        } else {
                            inductionLayoutBinding.email.visibility = View.VISIBLE
                        }
                        Log.d(TAG, inductionByList[position])
                    }

                    inductionLayoutBinding.inductionByAutoCompleteTextView.setOnClickListener {
                        inductionLayoutBinding.inductionByAutoCompleteTextView.showDropDown()
                    }
                    inductionLayoutBinding.inductionByAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            inductionLayoutBinding.inductionByAutoCompleteTextView.showDropDown()
                        }
                    }


                    /**Select Date*/

                    var selectedDate = ""

                    inductionLayoutBinding.date.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        val datePickerDialog = DatePickerDialog(
                            requireContext(),
                            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                                var month1 = month
                                selectedDate = String.format(
                                    Locale.ENGLISH, "%4d-%02d-%02d", year, ++month1, dayOfMonth
                                )
                                inductionLayoutBinding.date.setText(selectedDate)
                            },
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH],
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                        datePickerDialog.show()
                    }


                    /**Select Time*/

                    var selectedTime = ""
                    inductionLayoutBinding.time.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        val timePickerDialog = TimePickerDialog(
                            requireContext(), { view, hourOfDay, minute ->
                                // val selectedTime = "$hourOfDay:$minute"
                                selectedTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
                                inductionLayoutBinding.time.setText(selectedTime)

                            }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
                        )
                        timePickerDialog.show()
                    }


                    /**Search Email*/

                    inductionLayoutBinding.email.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            emailText: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            if (emailText.isNullOrEmpty()) {
                                inductionLayoutBinding.searchedEmailUserName.visibility = View.GONE
                                inductionLayoutBinding.searchedEmailUserName.text = null
                                searchedEmailUserID = null
                                return
                            }

                            if (isGmailAddress(emailText)) {
                                Log.d(TAG, emailText.toString())
                                searchedEmailUserID = null
                                counsellingViewModel.emailSearch(emailText.toString())

                                counsellingViewModel.emailSearchResponseLiveData.observe(
                                    viewLifecycleOwner
                                ) { it1 ->
                                    inductionLayoutBinding.spinKit.visibility = View.GONE
                                    when (it1) {
                                        is NetworkResult.Success -> {
                                            Log.d(TAG, it1.data!!.toString())
                                            inductionLayoutBinding.searchedEmailUserName.visibility =
                                                View.VISIBLE
                                            if (it1.data.user == null) {
                                                inductionLayoutBinding.searchedEmailUserName.text =
                                                    "Email Not Found !"
                                                inductionLayoutBinding.searchedEmailUserName.setTextColor(
                                                    Color.RED
                                                )
                                                return@observe
                                            }
                                            inductionLayoutBinding.searchedEmailUserName.setTextColor(
                                                resources.getColor(
                                                    R.color.parrotGreen,
                                                    null
                                                )
                                            )

                                            inductionLayoutBinding.searchedEmailUserName.text =
                                                it1.data.user.name
                                            searchedEmailUserID = it1.data.user.userID.toString()

                                            counsellingViewModel.emailSearchResponseLiveData.removeObservers(
                                                viewLifecycleOwner
                                            )

                                        }

                                        is NetworkResult.Error -> {
                                            inductionLayoutBinding.searchedEmailUserName.text =
                                                it1.message
                                            inductionLayoutBinding.searchedEmailUserName.setTextColor(
                                                Color.RED
                                            )
                                            Toast.makeText(
                                                requireContext(),
                                                it1.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.d(TAG, it1.message.toString())
                                            counsellingViewModel.emailSmsResponseLiveData.removeObservers(
                                                viewLifecycleOwner
                                            )
                                        }

                                        is NetworkResult.Loading -> {
                                            inductionLayoutBinding.spinKit.visibility = View.VISIBLE
                                        }
                                    }
                                }

                            } else {
                                inductionLayoutBinding.searchedEmailUserName.visibility = View.GONE
                                inductionLayoutBinding.searchedEmailUserName.text = null
                                searchedEmailUserID = null
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {

                        }

                    })


                    inductionLayoutBinding.cancelButton.setOnClickListener { materialAlertDialog.dismiss() }
                    inductionLayoutBinding.close.setOnClickListener { materialAlertDialog.dismiss() }
                    inductionLayoutBinding.submit.setOnClickListener {
                        if (inductionLayoutBinding.meetLink.text.isNullOrEmpty()) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog.contentText = "Please Fill Meeting Link"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        if (selectedPositionInductionBy == null) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog.contentText = "Please Select Induction By Type"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        var userReference = ""
                        if (selectedPositionInductionBy == 0) {
                            userReference = "self"
                        } else {
                            if (searchedEmailUserID.isNullOrEmpty()) {
                                val sweetAlertDialog =
                                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                sweetAlertDialog.contentText =
                                    "Please search Email because other is selected for Induction By"
                                sweetAlertDialog.confirmText = "OK"
                                sweetAlertDialog.show()
                                return@setOnClickListener
                            }
                            userReference = searchedEmailUserID.toString()
                        }
                        if (selectedDate.isEmpty()) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog.contentText = "Please Select Date"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        if (selectedTime.isEmpty()) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog.contentText = "Please Select Time"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        val inductionRequest = InductionRequest(
                            selectedDate,
                            null,
                            "C",
                            inductionLayoutBinding.meetLink.text.toString(),
                            selectedTime,
                            uid,
                            userReference
                        )
                        Log.d(TAG, inductionRequest.toString())
                        counsellingViewModel.inductionRequest(inductionRequest)


                        val sweetProgressDialog =
                            SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
                        sweetProgressDialog.setCanceledOnTouchOutside(false)
                        counsellingViewModel.inductionResponseLiveData.observe(viewLifecycleOwner) { it1 ->
                            sweetProgressDialog.dismiss()
                            when (it1) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, it1.data!!.toString())
                                    materialAlertDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        it1.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    counsellingViewModel.inductionResponseLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )
                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        it1.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(TAG, it1.message.toString())
                                    counsellingViewModel.inductionResponseLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )
                                }

                                is NetworkResult.Loading -> {
                                    sweetProgressDialog.show()
                                }
                            }
                        }
                    }


                }

            }
            true
        }
        popupMenu.show()

    }


    private fun getCounsellingData() {
        // val dataRequest = DataRequest("counselling")
        counsellingViewModel.getCounsellingData("counselling", 1)
    }

    private fun populateTemplateSpinnerEmail(emailTemplates: List<CRMResponse.Data.Email>) {

        templateMap.clear()
        templateMap["Select Template"] = 0
        for (i in emailTemplates) {
            templateMap[i.template] = i.templateID
        }
        Log.d(TAG, templateMap.toString())
        val whatsappTemplateAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, templateMap.keys.toList()
        )

        whatsappTemplateAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        sendEmailLayoutBinding.templateSpinner.adapter = whatsappTemplateAdapter

        sendEmailLayoutBinding.templateSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    //  sendSmsLayoutBinding.message.setText(templateMap[sendSmsLayoutBinding.templateSpinner.selectedItem]!!)
                    templateId = templateMap[sendEmailLayoutBinding.templateSpinner.selectedItem]!!
                    if (templateId != 0) {
                        counsellingViewModel.getTemplateContent(templateId, uid)
                    } else {
                        sendEmailLayoutBinding.message.text = null
                        sendEmailLayoutBinding.subject.text = null
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

    }

    private fun populateTemplateSpinnerWhatsApp(whatsappTemplates: List<CRMResponse.Data.Whatsapp>) {
        templateMap.clear()
        templateMap["Select Template"] = 0
        for (i in whatsappTemplates) {
            templateMap[i.template] = i.templateID
        }
        Log.d(TAG, templateMap.toString())
        val whatsappTemplateAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, templateMap.keys.toList()
        )

        whatsappTemplateAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        sendSmsLayoutBinding.templateSpinner.adapter = whatsappTemplateAdapter

        sendSmsLayoutBinding.templateSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    //  sendSmsLayoutBinding.message.setText(templateMap[sendSmsLayoutBinding.templateSpinner.selectedItem]!!)
                    templateId = templateMap[sendSmsLayoutBinding.templateSpinner.selectedItem]!!
                    if (templateId != 0) {
                        counsellingViewModel.getTemplateContent(templateId, uid)
                    } else {
                        sendSmsLayoutBinding.message.text = null
                        sendSmsLayoutBinding.variableList.text = null
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

    }

    private fun setTime() {
        if (callClicked) {
            makeCallLayoutBinding.time.setOnClickListener { v ->
                val calendar = Calendar.getInstance()

                val timePickerDialog = TimePickerDialog(
                    requireContext(), { view, hourOfDay, minute ->
                        // val selectedTime = "$hourOfDay:$minute"
                        makeCallLayoutBinding.time.setText(
                            String.format(
                                "%02d:%02d", hourOfDay, minute
                            ) + ":00"
                        )
                    }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
                )

                timePickerDialog.show()
            }
        } else if (smsClicked || whatsappClicked) {
            sendSmsLayoutBinding.time.setOnClickListener { v ->
                val calendar = Calendar.getInstance()

                val timePickerDialog = TimePickerDialog(
                    requireContext(), { view, hourOfDay, minute ->
                        // val selectedTime = "$hourOfDay:$minute"
                        sendSmsLayoutBinding.time.setText(
                            String.format(
                                "%02d:%02d", hourOfDay, minute
                            ) + ":00"
                        )
                    }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
                )

                timePickerDialog.show()

            }
        } else if (emailClicked) {
            sendEmailLayoutBinding.time.setOnClickListener { v ->
                val calendar = Calendar.getInstance()

                val timePickerDialog = TimePickerDialog(
                    requireContext(), { view, hourOfDay, minute ->
                        // val selectedTime = "$hourOfDay:$minute"
                        sendEmailLayoutBinding.time.setText(
                            String.format(
                                "%02d:%02d", hourOfDay, minute
                            ) + ":00"
                        )
                    }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false)

                timePickerDialog.show()
            }
        }
    }

    private fun setFollowDate() {
        if (callClicked) {
            makeCallLayoutBinding.followUpDate.setOnClickListener { v ->
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                        var month1 = month
                        val followDate = String.format(
                            Locale.ENGLISH, "%02d-%02d-%4d", dayOfMonth, ++month1, year
                        )
                        makeCallLayoutBinding.followUpDate.setText(followDate)
                    },
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog.show()
            }
        } else if (smsClicked || whatsappClicked) {
            sendSmsLayoutBinding.followUpDate.setOnClickListener { v ->
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                        var month1 = month
                        val followDate = String.format(
                            Locale.ENGLISH, "%02d-%02d-%4d", dayOfMonth, ++month1, year
                        )
                        sendSmsLayoutBinding.followUpDate.setText(followDate)
                    },
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog.show()
            }
        } else if (emailClicked) {
            sendEmailLayoutBinding.followUpDate.setOnClickListener { v ->
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                        var month1 = month
                        val followDate = String.format(
                            Locale.ENGLISH, "%02d-%02d-%4d", dayOfMonth, ++month1, year
                        )
                        sendEmailLayoutBinding.followUpDate.setText(followDate)
                    },
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                datePickerDialog.show()
            }
        }

    }

    private fun populateStatusSpinner(status: List<CRMResponse.Data.Status>) {
        for (i in status) {
            statusMap[i.action] = i.status
        }
        Log.d(TAG, statusMap.toString())

        val statusAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item, statusMap.keys.toList())
        statusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        if (emailClicked) {
            sendEmailLayoutBinding.statusSpinner.adapter = statusAdapter
        } else if (smsClicked || whatsappClicked) sendSmsLayoutBinding.statusSpinner.adapter =
            statusAdapter
        else if (callClicked) {
            makeCallLayoutBinding.statusSpinner.adapter = statusAdapter
        }


    }

    private fun populateTemplateSpinnerSms(smsTemplates: List<CRMResponse.Data.Sms>) {

        templateMap.clear()

        templateMap["Select Template"] = 0
        for (i in smsTemplates) {
            templateMap[i.template] = i.templateID
        }
        Log.d(TAG, templateMap.toString())
        val smsTemplateAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, templateMap.keys.toList()
        )

        smsTemplateAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        sendSmsLayoutBinding.templateSpinner.adapter = smsTemplateAdapter

        sendSmsLayoutBinding.templateSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    //  sendSmsLayoutBinding.message.setText(templateMap[sendSmsLayoutBinding.templateSpinner.selectedItem]!!)
                    templateId = templateMap[sendSmsLayoutBinding.templateSpinner.selectedItem]!!
                    if (templateId != 0) {
                        counsellingViewModel.getTemplateContent(templateId, uid)
                    } else sendSmsLayoutBinding.message.text = null

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

    }

    private fun makeCall(uid: Int, userName: String) {
        whatsappClicked = false
        smsClicked = false
        callClicked = true
        emailClicked = false
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())

        makeCallLayoutBinding = MakeCallLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        dialogBuilder.setView(makeCallLayoutBinding.root)
        materialAlertDialog = dialogBuilder.create()
        materialAlertDialog.show()


        makeCallLayoutBinding.userName.text = userName

        makeCallLayoutBinding.callButton.setOnClickListener {
            val callRequest = CallRequest(uid)
            Log.d(TAG, callRequest.toString())
            counsellingViewModel.makeCall(callRequest)
        }
        materialAlertDialog.setCanceledOnTouchOutside(false)
        makeCallLayoutBinding.close.setOnClickListener {
            materialAlertDialog.hide()
        }
        makeCallLayoutBinding.submit.setOnClickListener {
            val smsRequest = SmsRequest(
                "C",
                "CM",
                null,
                makeCallLayoutBinding.followUpDate.text.toString(),
                null,
                makeCallLayoutBinding.remarks.text.toString(),
                statusMap[makeCallLayoutBinding.statusSpinner.selectedItem.toString()],
                null,
                makeCallLayoutBinding.time.text.toString(),
                uid.toString()
            )
            if(! validateSubmitCallRequest(smsRequest)) return@setOnClickListener
            counsellingViewModel.sendSms(smsRequest)


            materialAlertDialog.dismiss()
        }
        counsellingViewModel.getCrmContact()

    }

    private fun sendCloudMessage(uid: Int, userName: String) {
        smsClicked = true
        whatsappClicked = false
        emailClicked = false
        callClicked = false
        this.uid = uid
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        sendSmsLayoutBinding = SendSmsLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        dialogBuilder.setView(sendSmsLayoutBinding.root)
        sendSmsLayoutBinding.variableList.visibility = View.GONE
        sendSmsLayoutBinding.variableText.visibility = View.GONE
        sendSmsLayoutBinding.userName.text = userName

        materialAlertDialog = dialogBuilder.create()
        sendSmsLayoutBinding.submit.setOnClickListener {
            val smsRequest = SmsRequest(
                "C",
                "SM",
                sendSmsLayoutBinding.message.text.toString(),
                sendSmsLayoutBinding.followUpDate.text.toString(),
                null,
                sendSmsLayoutBinding.remarks.text.toString(),
                statusMap[sendSmsLayoutBinding.statusSpinner.selectedItem],
                templateId.toString(),
                sendSmsLayoutBinding.time.text.toString(),
                uid.toString()
            )
            if(! validateSendSmsOrWhatsAppRequest(smsRequest)) return@setOnClickListener
            Log.d(TAG, smsRequest.toString())
            counsellingViewModel.sendSms(smsRequest)

            materialAlertDialog.hide()
        }
        sendSmsLayoutBinding.close.setOnClickListener {
            materialAlertDialog.hide()
        }
        sendSmsLayoutBinding.cancelButton.setOnClickListener {
            materialAlertDialog.hide()
        }
        materialAlertDialog.show()
        counsellingViewModel.getCrmContact()


    }

    private fun sendWhatsappMessage(uid: Int, userName: String) {
        this.uid = uid
        whatsappClicked = true
        smsClicked = false
        emailClicked = false
        callClicked = false
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        sendSmsLayoutBinding = SendSmsLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBuilder.setView(sendSmsLayoutBinding.root)
        sendSmsLayoutBinding.variableList.visibility = View.VISIBLE
        sendSmsLayoutBinding.variableText.visibility = View.VISIBLE
        sendSmsLayoutBinding.userName.text = userName

        materialAlertDialog = dialogBuilder.create()

        sendSmsLayoutBinding.submit.setOnClickListener {
            val smsRequest = SmsRequest(
                "C",
                "WM",
                sendSmsLayoutBinding.variableList.text.toString(),
                sendSmsLayoutBinding.followUpDate.text.toString(),
                null,
                sendSmsLayoutBinding.remarks.text.toString(),
                statusMap[sendSmsLayoutBinding.statusSpinner.selectedItem],
                templateId.toString(),
                sendSmsLayoutBinding.time.text.toString(),
                uid.toString()
            )
            if(! validateSendSmsOrWhatsAppRequest(smsRequest)) return@setOnClickListener
            Log.d(TAG, smsRequest.toString())
            counsellingViewModel.sendSms(smsRequest)
            materialAlertDialog.hide()
        }
        sendSmsLayoutBinding.close.setOnClickListener {
            materialAlertDialog.hide()
        }
        sendSmsLayoutBinding.cancelButton.setOnClickListener {
            materialAlertDialog.hide()
        }
        materialAlertDialog.show()
        counsellingViewModel.getCrmContact()

    }

    private fun sendEmailMessage(uid: Int, userName: String) {
        this.uid = uid
        whatsappClicked = false
        smsClicked = false
        callClicked = false
        emailClicked = true
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        sendEmailLayoutBinding =
            SendEmailLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBuilder.setView(sendEmailLayoutBinding.root)
        sendEmailLayoutBinding.userName.text = userName

        materialAlertDialog = dialogBuilder.create()
        setSmtpSpinnerAdapter()


        sendEmailLayoutBinding.close.setOnClickListener {
            materialAlertDialog.dismiss()
        }
        sendEmailLayoutBinding.cancelButton.setOnClickListener {
            materialAlertDialog.dismiss()
        }
        sendEmailLayoutBinding.submit.setOnClickListener { _ ->
            val sendEmailRequest = SendEmailRequest(
                "C",
                "EM",
                templateHtmlText,
                if(sendEmailLayoutBinding.addFooterFlag.isChecked) "true" else "false",
                sendEmailLayoutBinding.followUpDate.text.toString(),
                null,
                sendEmailLayoutBinding.remarks.text.toString(),
                sendEmailLayoutBinding.smtpSpinner.selectedItem.toString(),
                statusMap[sendEmailLayoutBinding.statusSpinner.selectedItem],
                sendEmailLayoutBinding.subject.text.toString(),
                templateId.toString(),
                sendEmailLayoutBinding.time.text.toString(),
                uid.toString()
            )
            if(! validateSendEmailRequest(sendEmailRequest)) return@setOnClickListener
            Log.d(TAG, sendEmailRequest.toString())
            counsellingViewModel.sendEmailMessage(sendEmailRequest)
            materialAlertDialog.dismiss()
        }

        materialAlertDialog.show()
        counsellingViewModel.getCrmContact()

    }

    private fun setSmtpSpinnerAdapter() {
        val smtpAdapter =
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.smtpArray,
                R.layout.spinner_item
            )
        smtpAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        sendEmailLayoutBinding.smtpSpinner.adapter = smtpAdapter
    }

    /*
    private fun scheduleNotification(timeInMillis: Long, contentText: String) {

        if (Calendar.getInstance().timeInMillis > timeInMillis) {
            return
        }

        createNotificationChannel()
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(requireContext(), NotificationReceiver::class.java)
        notificationIntent.putExtra("contentText", contentText)

        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity(), 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        try {

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)


        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val description = "Channel for Alarm Manager"
            val channel =
                NotificationChannel(Constants.CHANNELID, name, NotificationManager.IMPORTANCE_HIGH)

            channel.description = description
            val notificationManager =
                requireContext().getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)

        }

    }
*/


    private fun validateSubmitCallRequest(request: SmsRequest) : Boolean{
        val dialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)

        if(request.remark.isEmpty()){
            dialog.contentText = "Please Write Some Remark"
            dialog.show()
            return false
        }
        else if(request.date.isEmpty()){
            dialog.contentText = "Please Select Follow Up Date"
            dialog.show()
            return false
        }
        else if(request.time.isEmpty()){
            dialog.contentText = "Please Select Time"
            dialog.show()
            return false
        }
        else if(request.status == null){
            dialog.contentText = "Please Select Status"
            dialog.show()
            return false
        }
        return true
    }

    private fun validateSendSmsOrWhatsAppRequest(request: SmsRequest) : Boolean{
        val dialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)

        if(request.content == null || request.template_id == "0"){
            dialog.contentText = "Please Select Template"
            dialog.show()
            return false
        }
        else if(request.remark.isEmpty()){
            dialog.contentText = "Please Write Some Remark"
            dialog.show()
            return false
        }
        else if(request.date.isEmpty()){
            dialog.contentText = "Please Select Follow Up Date"
            dialog.show()
            return false
        }
        else if(request.time.isEmpty()){
            dialog.contentText = "Please Select Time"
            dialog.show()
            return false
        }
        else if(request.status == null){
            dialog.contentText = "Please Select Status"
            dialog.show()
            return false
        }
        return true
    }

    private fun validateSendEmailRequest(request: SendEmailRequest) : Boolean{
        val dialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)

        if(request.content.isNullOrEmpty() || request.template_id == "0" || request.subject.isEmpty()){
            dialog.contentText = "Please Select Template"
            dialog.show()
            return false
        }
        else if(request.remark.isEmpty()){
            dialog.contentText = "Please Write Some Remark"
            dialog.show()
            return false
        }
        else if(request.date.isEmpty()){
            dialog.contentText = "Please Select Follow Up Date"
            dialog.show()
            return false
        }
        else if(request.time.isEmpty()){
            dialog.contentText = "Please Select Time"
            dialog.show()
            return false
        }
        else if(request.status == null){
            dialog.contentText = "Please Select Status"
            dialog.show()
            return false
        }

        return true
    }



    private fun isGmailAddress(email: CharSequence): Boolean {
        val regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$".toRegex()
        return regex.matches(email)
    }


}