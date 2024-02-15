package com.margdarshakendra.margdarshak.dashboard_bottom_fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.margdarshakendra.margdarshak.DashboardActivity
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.adapters.HiringPagingAdapter
import com.margdarshakendra.margdarshak.adapters.PagingLoadingAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentHiringBinding
import com.margdarshakendra.margdarshak.databinding.GiveCommunicationTestLinkLayoutBinding
import com.margdarshakendra.margdarshak.databinding.InductionLayoutBinding
import com.margdarshakendra.margdarshak.databinding.MakeCallLayoutBinding
import com.margdarshakendra.margdarshak.databinding.SendEmailLayoutBinding
import com.margdarshakendra.margdarshak.databinding.SendSmsLayoutBinding
import com.margdarshakendra.margdarshak.databinding.ShortlistForHiringDialogLayoutBinding
import com.margdarshakendra.margdarshak.databinding.SkillTestLinkDialogLayoutBinding
import com.margdarshakendra.margdarshak.interview_fragments.HrInterviewFragment
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.GiveCommunicationTestLinkRequest
import com.margdarshakendra.margdarshak.models.GiveDocsUploadLinkRequest
import com.margdarshakendra.margdarshak.models.GiveSkillTestLinkRequest
import com.margdarshakendra.margdarshak.models.InductionRequest
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.ShortListUserRequest
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.Constants.USERMEETLINK
import com.margdarshakendra.margdarshak.utils.HtmlImageGetter
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.HiringViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HiringFragment : Fragment() {

    private val hiringViewModel by viewModels<HiringViewModel>()

    private lateinit var materialAlertDialog: AlertDialog
    private var searchedEmailUserID: String? = null
    private var selectedPositionInductionBy: Int? = null

    private lateinit var sendSmsLayoutBinding: SendSmsLayoutBinding
    private lateinit var sendEmailLayoutBinding: SendEmailLayoutBinding
    private lateinit var makeCallLayoutBinding: MakeCallLayoutBinding
    private lateinit var skillTestLinkDialogLayoutBinding: SkillTestLinkDialogLayoutBinding

    private var templateId = 0
    private var templateHtmlText = ""
    private var employerId = 0
    private var employerPostId = 0
    private var uid = 0
    private var hireID = 0

    private lateinit var templateMap: LinkedHashMap<String, Int>
    private lateinit var statusMap: LinkedHashMap<String, String>
    private lateinit var employerMap: LinkedHashMap<String, Int>

    private var whatsappClicked = false
    private var smsClicked = false
    private var emailClicked = false
    private var callClicked = false

    private lateinit var binding: FragmentHiringBinding

    private lateinit var hiringPagingAdapter: HiringPagingAdapter
    //private lateinit var hiringDataRecAdapter: HiringDataRecAdapter

    private var isDataFiltered = false

    @Inject
    lateinit var sharedPreference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHiringBinding.inflate(inflater, container, false)

        templateMap = LinkedHashMap()
        statusMap = LinkedHashMap()
        employerMap = LinkedHashMap()

        skillTestLinkDialogLayoutBinding =
            SkillTestLinkDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        sendSmsLayoutBinding = SendSmsLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        sendEmailLayoutBinding =
            SendEmailLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        makeCallLayoutBinding = MakeCallLayoutBinding.inflate(LayoutInflater.from(requireContext()))


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener {
            if( ! isDataFiltered){
                hiringPagingAdapter.refresh()
            }
            else{
                viewLifecycleOwner.lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                        hiringViewModel.hiringOrFilteredPagingFlow(mode = "hiring").collect{
                            binding.isFilteredOrAllText.text = "All"
                            isDataFiltered = false
                            hiringPagingAdapter.submitData(it)
                        }
                    }
                }
            }
        }

        getHiringData()  // it is called for only get employer data

        hiringPagingAdapter = HiringPagingAdapter(
            requireContext(),
            ::makeCall,
            ::sendCloudMessage,
            ::sendWhatsappMessage,
            ::sendEmailMessage,
            ::showPopMenu
        )


        hiringPagingAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                   // skeleton.showSkeleton()
                    Log.d(TAG, "screen data loading")
                }

                is LoadState.Error -> {
                   // skeleton.showOriginal()
                    binding.swipeRefresh.isRefreshing = false
                    val error =
                        "Error: ${(loadState.refresh as LoadState.Error).error.localizedMessage}"
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, error)
                }

                is LoadState.NotLoading -> {
                    binding.swipeRefresh.isRefreshing = false
                    //skeleton.showOriginal()
                    if(binding.hiringDataRecyclerView.adapter == null){
                        binding.hiringDataRecyclerView.setHasFixedSize(true)
                        binding.hiringDataRecyclerView.adapter = hiringPagingAdapter.withLoadStateHeaderAndFooter(
                            header = PagingLoadingAdapter(),
                            footer = PagingLoadingAdapter()
                        )
                        Log.d(TAG, "adapter is null !")
                    }else Log.d(TAG, "adapter is not null !")
                    Log.d(TAG, "screen data received")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                hiringViewModel.hiringOrFilteredPagingFlow(mode = "hiring").collect{
                    hiringPagingAdapter.submitData(it)
                }
            }
        }

        /*hiringViewModel.hiringPagingList.observe(viewLifecycleOwner) {
            Log.d(TAG, "screen $it")
            hiringPagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }*/


        hiringViewModel.hiringDataResponseLiveData.observe(viewLifecycleOwner) {
            //skeleton.showOriginal()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    //  binding.swipeRefresh.isRefreshing = false
                    //  hiringPagingAdapter.submitData(lifecycle, PagingData.from(it.data.data.data))


                    if (!it.data.employers.isNullOrEmpty()) {
                        for (i in it.data.employers) {
                            if (i != null) employerMap[i.name] = i.userID
                        }
                    } else return@observe

                    /*val employerListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        employerMap.keys.toList()
                    )
                    employerListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.employerAutoCompleteTextView.setAdapter(employerListAdapter)

                    binding.employerAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        employerId =
                            employerMap[binding.employerAutoCompleteTextView.text.toString()]!!
                        binding.empPostAutoCompleteTextView.setAdapter(null)
                        binding.empPostAutoCompleteTextView.text = null
                        getFilterPosts(employerId)
                        Log.d(TAG, employerId.toString())
                    }
                    binding.employerAutoCompleteTextView.setOnClickListener {
                        binding.employerAutoCompleteTextView.showDropDown()
                    }
                    binding.employerAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.employerAutoCompleteTextView.showDropDown()
                        }
                    }
*/
                }

                is NetworkResult.Error -> {
                    //  binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    // skeleton.showSkeleton()
                }
            }
        }


        binding.filterBtn.setOnClickListener{

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
                shortlistForHiringDialogLayoutBinding.buttonRl.isVisible = false

                materialAlertDialog.setCanceledOnTouchOutside(false)

                shortlistForHiringDialogLayoutBinding.userName.text = null
                shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setAdapter(null)
                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setAdapter(null)

                val employerListAdapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_dropdown_item,
                    employerMap.keys.toList()
                )
                employerListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setAdapter(employerListAdapter)

                shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                    employerId =
                        employerMap[shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.text.toString()]!!
                    shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setAdapter(null)
                    shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.text = null
                    employerPostId = 0
                    getFilterPosts(employerId)
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

                hiringViewModel.filteredPostsDataLiveData.observe(viewLifecycleOwner) { response ->
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

                                if(employerId == 0) return@setOnItemClickListener

                                viewLifecycleOwner.lifecycleScope.launch {
                                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                                        hiringViewModel.hiringOrFilteredPagingFlow(employerId = employerId, postId =  employerPostId).collectLatest {
                                            isDataFiltered = true
                                            binding.isFilteredOrAllText.text = "Filtered"
                                            materialAlertDialog.dismiss()
                                            hiringPagingAdapter.submitData(it)
                                        }
                                    }
                                }

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

                /*shortlistForHiringDialogLayoutBinding.submit.setOnClickListener {

                    getHiringFilteredData()
                    hiringViewModel.hiringFilteredDataLiveData.observe(viewLifecycleOwner) { response ->
                       // skeleton.showOriginal()
                        when (response) {
                            is NetworkResult.Success -> {
                                Log.d(TAG, response.data!!.toString())

                                isDataFiltered = true
                                binding.isFilteredOrAllText.text = "Filtered"

                                hiringDataRecAdapter = HiringDataRecAdapter(
                                    requireContext(),
                                    ::makeCall,
                                    ::sendCloudMessage,
                                    ::sendWhatsappMessage,
                                    ::sendEmailMessage,
                                    ::showPopMenu
                                )
                                
                                if(response.data.data.isNullOrEmpty()){
                                    Toast.makeText(requireContext(), "List is Null", Toast.LENGTH_SHORT).show()
                                }

                                binding.hiringDataRecyclerView.adapter = null
                                binding.hiringDataRecyclerView.adapter = hiringDataRecAdapter
                                hiringDataRecAdapter.submitList(null)
                                hiringDataRecAdapter.submitList(response.data.data)

                                materialAlertDialog.dismiss()
                                hiringViewModel.hiringFilteredDataLiveData.removeObservers(
                                    viewLifecycleOwner
                                )
                                hiringViewModel.filteredPostsDataLiveData.removeObservers(
                                    viewLifecycleOwner
                                )

                            }

                            is NetworkResult.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d(TAG, response.message.toString())
                                materialAlertDialog.dismiss()
                                hiringViewModel.hiringFilteredDataLiveData.removeObservers(
                                    viewLifecycleOwner
                                )
                                hiringViewModel.filteredPostsDataLiveData.removeObservers(
                                    viewLifecycleOwner
                                )
                                return@observe
                            }

                            is NetworkResult.Loading -> {
                               // skeleton.showSkeleton()
                            }
                        }
                    }

                }
*/
                materialAlertDialog.setOnDismissListener {
                    hiringViewModel.filteredPostsDataLiveData.removeObservers(viewLifecycleOwner)
                    hiringViewModel.hiringDataResponseLiveData.removeObservers(viewLifecycleOwner)
                }

            }


        /*hiringViewModel.hiringDataResponseLiveData.observe(viewLifecycleOwner) {
            //skeleton.showOriginal()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    //  binding.swipeRefresh.isRefreshing = false
                    //  hiringPagingAdapter.submitData(lifecycle, PagingData.from(it.data.data.data))


                    if (!it.data.employers.isNullOrEmpty()) {
                        for (i in it.data.employers) {
                            if (i != null) employerMap[i.name] = i.userID
                        }
                    } else return@observe

                    */
        /*val employerListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        employerMap.keys.toList()
                    )
                    employerListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.employerAutoCompleteTextView.setAdapter(employerListAdapter)

                    binding.employerAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        employerId =
                            employerMap[binding.employerAutoCompleteTextView.text.toString()]!!
                        binding.empPostAutoCompleteTextView.setAdapter(null)
                        binding.empPostAutoCompleteTextView.text = null
                        getFilterPosts(employerId)
                        Log.d(TAG, employerId.toString())
                    }
                    binding.employerAutoCompleteTextView.setOnClickListener {
                        binding.employerAutoCompleteTextView.showDropDown()
                    }
                    binding.employerAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.employerAutoCompleteTextView.showDropDown()
                        }
                    }
*/
        /*
                }

                is NetworkResult.Error -> {
                    //  binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                   // skeleton.showSkeleton()
                }
            }
        }*/

        /*hiringViewModel.hiringFilteredDataLiveData.observe(viewLifecycleOwner) {
                skeleton.showOriginal()
                when (it) {
                    is NetworkResult.Success -> {
                        Log.d(TAG, it.data!!.toString())

                        hiringDataRecAdapter.submitList(null)
                        binding.hiringDataRecyclerView.adapter = null
                        binding.hiringDataRecyclerView.adapter = hiringDataRecAdapter
                        hiringDataRecAdapter.submitList(it.data.data)

                    }

                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, it.message.toString())
                    }

                    is NetworkResult.Loading -> {
                        skeleton.showSkeleton()
                    }
                }
            }*/

        /*hiringViewModel.filteredPostsDataLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    val employerPostMap = LinkedHashMap<String, Int>()

                    if (!it.data.data.isNullOrEmpty()) {
                        for (i in it.data.data) {
                            employerPostMap[i.position] = i.postID
                        }
                    } else return@observe

                    val employerPostListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        employerPostMap.keys.toList()
                    )
                    employerPostListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.empPostAutoCompleteTextView.setAdapter(employerPostListAdapter)

                    binding.empPostAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        employerPostId =
                            employerPostMap[binding.empPostAutoCompleteTextView.text.toString()]!!
                        Log.d(TAG, employerPostId.toString())

                        getHiringFilteredData()
                    }
                    binding.empPostAutoCompleteTextView.setOnClickListener {
                        binding.empPostAutoCompleteTextView.showDropDown()
                    }
                    binding.empPostAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.empPostAutoCompleteTextView.showDropDown()
                        }
                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }*/

        val progressDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        progressDialog.contentText = "Please Wait..."
        progressDialog.setCanceledOnTouchOutside(false)
        hiringViewModel.callResponseLiveData.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.data.toString())
                    Toast.makeText(requireContext(), it.data.data.message, Toast.LENGTH_SHORT).show()
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

        hiringViewModel.crmResponseLiveData.observe(viewLifecycleOwner) {
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

        hiringViewModel.smsResponseLiveData.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()

                    /*
                                        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault())

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
                                            Log.d(TAG, onlyTimeFormat.format(calendar.time))
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


        hiringViewModel.emailSmsResponseLiveData.observe(viewLifecycleOwner) {
            progressDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()

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
                         Log.d(TAG, onlyTimeFormat.format(calendar.time))

                     }*/

                    /*binding.hiringDataRecyclerView.adapter = null
                        binding.hiringDataRecyclerView.adapter = hiringPagingAdapter.withLoadStateHeaderAndFooter(
                            header = PagingLoadingAdapter(),
                            footer = PagingLoadingAdapter()
                        )
                        hiringPagingAdapter.refresh()
                        isDataFiltered = false*/

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

        hiringViewModel.templateResponseLiveData.observe(viewLifecycleOwner) {
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
                            it.data.template,
                            HtmlCompat.FROM_HTML_MODE_LEGACY,
                            imageGetter,
                            null
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

    }

    private fun showSuccessAndRefreshActivity(contentText: String){
        val successDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
        successDialog.contentText = contentText
        successDialog.show()
        successDialog.setOnDismissListener {
            val intent = Intent(requireContext(), DashboardActivity::class.java)
            intent.putExtra("OpenFragment", "hiring" )
            requireActivity().startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requireActivity().window.setWindowAnimations(AppCompatActivity.OVERRIDE_TRANSITION_OPEN)
                requireActivity().overrideActivityTransition(AppCompatActivity.OVERRIDE_TRANSITION_OPEN,0,0)
            }
            else requireActivity().overridePendingTransition(0,0)
            requireActivity().finishAffinity()
            /*binding.hiringDataRecyclerView.adapter = null
                        binding.hiringDataRecyclerView.adapter = hiringPagingAdapter.withLoadStateHeaderAndFooter(
                            header = PagingLoadingAdapter(),
                            footer = PagingLoadingAdapter()
                        )
                        hiringPagingAdapter.refresh()
                        isDataFiltered = false*/
        }
    }

    private fun getFilterPosts(employerId: Int) {
        hiringViewModel.getFilteredPostsData(employerId)
    }

    private fun getHiringFilteredData() {
        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
        sweetAlertDialog.confirmText = "OK"
        if (employerId == 0) {

            sweetAlertDialog.contentText = "Please Select Employer"
            sweetAlertDialog.show()
            return
        }
        if (employerPostId == 0) {
            sweetAlertDialog.contentText = "Please Select Employer Position"
            sweetAlertDialog.show()
            return
        }
        Log.d(TAG, "empId $employerId, empPostId $employerPostId")

    }

    private fun showPopMenu(view: View, hireID: Int, uid: Int, userName: String) {
        Log.d(TAG, "showPopMenu is clicked")
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.hiring_pop_menu, popupMenu.menu)

        if (isDataFiltered) {
            popupMenu.menu.findItem(R.id.giveSkillTestLink).isVisible = true
            popupMenu.menu.findItem(R.id.giveInterviewLink).isVisible = true
            popupMenu.menu.findItem(R.id.startInterview).isVisible = true
            popupMenu.menu.findItem(R.id.interview).isVisible = true
            popupMenu.menu.findItem(R.id.giveCommunicationTestLink).isVisible = true
            popupMenu.menu.findItem(R.id.giveDocsUploadLink).isVisible = true
            popupMenu.menu.findItem(R.id.shortList).isVisible = false

        } else {
            popupMenu.menu.findItem(R.id.giveSkillTestLink).isVisible = false
            popupMenu.menu.findItem(R.id.giveInterviewLink).isVisible = false
            popupMenu.menu.findItem(R.id.startInterview).isVisible = false
            popupMenu.menu.findItem(R.id.interview).isVisible = false
            popupMenu.menu.findItem(R.id.giveCommunicationTestLink).isVisible = false
            popupMenu.menu.findItem(R.id.giveDocsUploadLink).isVisible = false
            popupMenu.menu.findItem(R.id.shortList).isVisible = true
        }

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.giveSkillTestLink -> {
                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())

                    val skillTestLinkDialogLayoutBinding =
                        SkillTestLinkDialogLayoutBinding.inflate(
                            LayoutInflater.from(
                                requireContext()
                            )
                        )

                    dialogBuilder.setView(skillTestLinkDialogLayoutBinding.root)
                    val materialAlertDialog = dialogBuilder.create()
                    materialAlertDialog.show()

                    materialAlertDialog.setCanceledOnTouchOutside(false)

                    skillTestLinkDialogLayoutBinding.userName.text = userName

                    var testDate = ""
                    var skillID = 0

                    skillTestLinkDialogLayoutBinding.testDate.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        val datePickerDialog = DatePickerDialog(
                            requireContext(),
                            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                                var month1 = month
                                testDate = String.format(
                                    Locale.ENGLISH, "%4d-%02d-%02d", year, ++month1, dayOfMonth
                                )
                                skillTestLinkDialogLayoutBinding.testDate.setText(testDate)
                            },
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH],
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                        datePickerDialog.show()
                    }



                    if (materialAlertDialog.isShowing) {
                        hiringViewModel.getHiringSkills()
                        hiringViewModel.hiringSkillsLiveData.observe(viewLifecycleOwner) { response ->
                            when (response) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, response.data!!.toString())
                                    val skillMap = LinkedHashMap<String, Int>()

                                    for (i in response.data.skills) {
                                        skillMap[i.skill] = i.skillsID
                                    }
                                    val skillsListAdapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        skillMap.keys.toList()
                                    )
                                    skillsListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                                    skillTestLinkDialogLayoutBinding.skillAutoCompleteTextView.setAdapter(
                                        skillsListAdapter
                                    )

                                    skillTestLinkDialogLayoutBinding.skillAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                                        skillID =
                                            skillMap[skillTestLinkDialogLayoutBinding.skillAutoCompleteTextView.text.toString()]!!

                                        Log.d(TAG, skillID.toString())
                                    }
                                    skillTestLinkDialogLayoutBinding.skillAutoCompleteTextView.setOnClickListener {
                                        skillTestLinkDialogLayoutBinding.skillAutoCompleteTextView.showDropDown()
                                    }
                                    skillTestLinkDialogLayoutBinding.skillAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                                        if (hasFocus) {
                                            skillTestLinkDialogLayoutBinding.skillAutoCompleteTextView.showDropDown()
                                        }
                                    }

                                    hiringViewModel.hiringSkillsLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )

                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        response.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(TAG, response.message.toString())
                                    hiringViewModel.hiringSkillsLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )
                                }

                                is NetworkResult.Loading -> {

                                }
                            }
                        }
                    }



                    skillTestLinkDialogLayoutBinding.submit.setOnClickListener {
                        if (testDate.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Please Select a Date",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        if (skillID == 0) {
                            Toast.makeText(
                                requireContext(),
                                "Please Select a Skill",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        val giveSkillTestLinkRequest = GiveSkillTestLinkRequest(
                            hireID.toString(),
                            skillID.toString(),
                            testDate,
                            uid.toString()
                        )
                        Log.d(TAG, giveSkillTestLinkRequest.toString())
                        hiringViewModel.giveHiringSkillTestLink(giveSkillTestLinkRequest)


                        hiringViewModel.giveHiringSkillTestLinkLiveData.observe(
                            viewLifecycleOwner
                        ) { response ->
                            when (response) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, response.data!!.toString())
                                    materialAlertDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        response.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        response.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    materialAlertDialog.dismiss()
                                    Log.d(TAG, response.message.toString())
                                }

                                is NetworkResult.Loading -> {

                                }
                            }
                        }

                    }


                    materialAlertDialog.setOnDismissListener {
                        hiringViewModel.giveHiringSkillTestLinkLiveData.removeObservers(
                            viewLifecycleOwner
                        )
                        hiringViewModel.hiringSkillsLiveData.removeObservers(viewLifecycleOwner)
                    }

                    skillTestLinkDialogLayoutBinding.close.setOnClickListener {
                        materialAlertDialog.dismiss()
                    }

                    skillTestLinkDialogLayoutBinding.cancelButton.setOnClickListener {
                        materialAlertDialog.dismiss()
                    }

                }

                R.id.giveInterviewLink -> {

                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())

                    skillTestLinkDialogLayoutBinding =
                        SkillTestLinkDialogLayoutBinding.inflate(
                            LayoutInflater.from(
                                requireContext()
                            )
                        )

                    dialogBuilder.setView(skillTestLinkDialogLayoutBinding.root)
                    val materialAlertDialog = dialogBuilder.create()
                    materialAlertDialog.setCanceledOnTouchOutside(false)
                    materialAlertDialog.show()

                    skillTestLinkDialogLayoutBinding.skillAutoCompleteTextView.visibility =
                        View.GONE
                    skillTestLinkDialogLayoutBinding.skillText.visibility = View.GONE

                    skillTestLinkDialogLayoutBinding.userName.text = userName

                    var interviewDate = ""

                    skillTestLinkDialogLayoutBinding.testDate.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        val datePickerDialog = DatePickerDialog(
                            requireContext(),
                            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                                var month1 = month
                                interviewDate = String.format(
                                    Locale.ENGLISH, "%4d-%02d-%02d", year, ++month1, dayOfMonth
                                )
                                skillTestLinkDialogLayoutBinding.testDate.setText(interviewDate)
                            },
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH],
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                        datePickerDialog.show()
                    }

                    skillTestLinkDialogLayoutBinding.submit.setOnClickListener {
                        if (interviewDate.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Please Select Date",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        val giveInterviewLinkRequest = GiveSkillTestLinkRequest(
                            hireID.toString(),
                            null,
                            interviewDate,
                            uid.toString()
                        )
                        Log.d(TAG, giveInterviewLinkRequest.toString())

                        hiringViewModel.giveHiringInterviewLink(giveInterviewLinkRequest)


                        hiringViewModel.giveHiringInterviewTestLinkLiveData.observe(
                            viewLifecycleOwner
                        ) { response ->
                            when (response) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, response.data!!.toString())
                                    materialAlertDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        response.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        response.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    materialAlertDialog.dismiss()
                                    Log.d(TAG, response.message.toString())
                                }

                                is NetworkResult.Loading -> {

                                }
                            }
                        }

                    }

                    materialAlertDialog.setOnDismissListener {
                        hiringViewModel.giveHiringInterviewTestLinkLiveData.removeObservers(
                            viewLifecycleOwner
                        )
                    }

                    skillTestLinkDialogLayoutBinding.close.setOnClickListener {
                        materialAlertDialog.dismiss()
                    }

                    skillTestLinkDialogLayoutBinding.cancelButton.setOnClickListener {
                        materialAlertDialog.dismiss()
                    }


                }

                R.id.startInterview -> {
                    val hrInterviewFragment = HrInterviewFragment()
                    val uidBundle = Bundle()
                    uidBundle.putInt("uid", uid)
                    hrInterviewFragment.arguments = uidBundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, hrInterviewFragment).commit()
                }

                R.id.shortList -> {

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
                        hiringViewModel.getFilteredPostsData(employerId)
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

                    hiringViewModel.filteredPostsDataLiveData.observe(viewLifecycleOwner) { response ->
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
                        hiringViewModel.shortListUser(shortListUserRequest)
                        Log.d(TAG, shortListUserRequest.toString())

                        hiringViewModel.shortListUserResponseLiveData.observe(viewLifecycleOwner) { response ->
                            when (response) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, response.data!!.toString())
                                    Toast.makeText(
                                        requireContext(),
                                        response.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    materialAlertDialog.dismiss()
                                    hiringViewModel.shortListUserResponseLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )
                                    hiringViewModel.filteredPostsDataLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )

                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        response.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(TAG, response.message.toString())
                                    materialAlertDialog.dismiss()
                                    hiringViewModel.shortListUserResponseLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )
                                    hiringViewModel.filteredPostsDataLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )
                                    return@observe
                                }

                                is NetworkResult.Loading -> {

                                }
                            }
                        }

                    }

                    materialAlertDialog.setOnDismissListener {
                        hiringViewModel.shortListUserResponseLiveData.removeObservers(
                            viewLifecycleOwner
                        )
                        hiringViewModel.filteredPostsDataLiveData.removeObservers(viewLifecycleOwner)
                    }

                }

                R.id.interview -> {

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
                            USERMEETLINK,
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


                    /**
                     * Search Email
                     */

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
                                hiringViewModel.emailSearch(emailText.toString())

                                hiringViewModel.emailSearchResponseLiveData.observe(
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

                                            hiringViewModel.emailSearchResponseLiveData.removeObservers(
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
                                            hiringViewModel.emailSmsResponseLiveData.removeObservers(
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
                            hireID,
                            "H",
                            inductionLayoutBinding.meetLink.text.toString(),
                            selectedTime,
                            uid,
                            userReference
                        )
                        Log.d(TAG, inductionRequest.toString())
                        hiringViewModel.inductionRequest(inductionRequest)


                        val sweetProgressDialog =
                            SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
                        sweetProgressDialog.setCanceledOnTouchOutside(false)
                        hiringViewModel.inductionResponseLiveData.observe(viewLifecycleOwner) { it1 ->
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
                                    hiringViewModel.inductionResponseLiveData.removeObservers(
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
                                    hiringViewModel.inductionResponseLiveData.removeObservers(
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

                R.id.giveCommunicationTestLink -> {

                    /**Create Dialog*/

                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())

                    val giveCommunicationTestLinkLayoutBinding =
                        GiveCommunicationTestLinkLayoutBinding.inflate(
                            LayoutInflater.from(
                                requireContext()
                            )
                        )

                    dialogBuilder.setView(giveCommunicationTestLinkLayoutBinding.root)
                    val materialAlertDialog = dialogBuilder.create()
                    materialAlertDialog.show()

                    materialAlertDialog.setCanceledOnTouchOutside(false)

                    giveCommunicationTestLinkLayoutBinding.userName.text = userName

                    var testId = 0

                    if (materialAlertDialog.isShowing) {
                        hiringViewModel.getCommunicationTests()

                        hiringViewModel.communicationTestsLiveData.observe(viewLifecycleOwner) { it1 ->
                            when (it1) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, it1.data!!.toString())
                                    val communicationMap: HashMap<String, Int> = HashMap()
                                    for (i in it1.data.hireTest) {
                                        communicationMap[i.test] = i.testID
                                    }
                                    val communicationTestListAdapter = ArrayAdapter(
                                        requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        communicationMap.keys.toList()
                                    )
                                    communicationTestListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                                    giveCommunicationTestLinkLayoutBinding.testAutoCompleteTextView.setAdapter(
                                        communicationTestListAdapter
                                    )

                                    giveCommunicationTestLinkLayoutBinding.testAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                                        testId =
                                            communicationMap[giveCommunicationTestLinkLayoutBinding.testAutoCompleteTextView.text.toString()]!!
                                        Log.d(TAG, testId.toString())
                                    }

                                    giveCommunicationTestLinkLayoutBinding.testAutoCompleteTextView.setOnClickListener {
                                        giveCommunicationTestLinkLayoutBinding.testAutoCompleteTextView.showDropDown()
                                    }
                                    giveCommunicationTestLinkLayoutBinding.testAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                                        if (hasFocus) {
                                            giveCommunicationTestLinkLayoutBinding.testAutoCompleteTextView.showDropDown()
                                        }
                                    }

                                    hiringViewModel.communicationTestsLiveData.removeObservers(
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
                                    hiringViewModel.communicationTestsLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )
                                }

                                is NetworkResult.Loading -> {

                                }
                            }
                        }

                    }

                    /**Select Date*/

                    var selectedDate = ""

                    giveCommunicationTestLinkLayoutBinding.date.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        val datePickerDialog = DatePickerDialog(
                            requireContext(),
                            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                                var month1 = month
                                selectedDate = String.format(
                                    Locale.ENGLISH, "%4d-%02d-%02d", year, ++month1, dayOfMonth
                                )
                                giveCommunicationTestLinkLayoutBinding.date.setText(selectedDate)
                            },
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH],
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                        datePickerDialog.show()
                    }

                    giveCommunicationTestLinkLayoutBinding.cancelButton.setOnClickListener { materialAlertDialog.dismiss() }
                    giveCommunicationTestLinkLayoutBinding.close.setOnClickListener { materialAlertDialog.dismiss() }
                    giveCommunicationTestLinkLayoutBinding.submit.setOnClickListener {
                        Log.d(TAG, "test ID : $testId")
                        if (testId == 0) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog.contentText = "Please Select Test Type"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        if (selectedDate.isEmpty()) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog.contentText = "Please Select Date"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        val giveCommunicationTestLinkRequest =
                            GiveCommunicationTestLinkRequest(hireID, selectedDate, testId, uid)
                        Log.d(TAG, giveCommunicationTestLinkRequest.toString())
                        hiringViewModel.giveCommunicationTestLink(giveCommunicationTestLinkRequest)

                        /**
                         *Observe the response give communication Test Link
                         */

                        hiringViewModel.giveCommunicationTestLinkResponseLiveData.observe(
                            viewLifecycleOwner
                        ) { it1 ->
                            when (it1) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, it1.data!!.toString())
                                    materialAlertDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        it1.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        it1.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(TAG, it1.message.toString())
                                }

                                is NetworkResult.Loading -> {
                                }
                            }
                        }
                    }
                    materialAlertDialog.setOnDismissListener {
                        hiringViewModel.communicationTestsLiveData.removeObservers(
                            viewLifecycleOwner
                        )
                        hiringViewModel.giveCommunicationTestLinkResponseLiveData.removeObservers(
                            viewLifecycleOwner
                        )
                    }
                }

                R.id.giveDocsUploadLink -> {

                    /**Create Dialog*/

                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())

                    val docsUploadBinding =
                        GiveCommunicationTestLinkLayoutBinding.inflate(
                            LayoutInflater.from(
                                requireContext()
                            )
                        )

                    dialogBuilder.setView(docsUploadBinding.root)
                    docsUploadBinding.testAutoCompleteTextView.visibility = View.GONE
                    val materialAlertDialog = dialogBuilder.create()
                    materialAlertDialog.show()

                    materialAlertDialog.setCanceledOnTouchOutside(false)

                    docsUploadBinding.userName.text = userName

                    /**Select Date*/

                    var selectedDate = ""

                    docsUploadBinding.date.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        val datePickerDialog = DatePickerDialog(
                            requireContext(),
                            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                                var month1 = month
                                selectedDate = String.format(
                                    Locale.ENGLISH, "%4d-%02d-%02d", year, ++month1, dayOfMonth
                                )
                                docsUploadBinding.date.setText(selectedDate)
                            },
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH],
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                        datePickerDialog.show()
                    }

                    docsUploadBinding.cancelButton.setOnClickListener { materialAlertDialog.dismiss() }
                    docsUploadBinding.close.setOnClickListener { materialAlertDialog.dismiss() }
                    docsUploadBinding.submit.setOnClickListener {
                        if (selectedDate.isEmpty()) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            sweetAlertDialog.contentText = "Please Select Date"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        val giveDocsUploadLinkRequest =
                            GiveDocsUploadLinkRequest(hireID, selectedDate, uid)
                        Log.d(TAG, giveDocsUploadLinkRequest.toString())
                        hiringViewModel.giveDocsUploadLink(giveDocsUploadLinkRequest)

                        /**Observe the response give communication Test Link*/
                        hiringViewModel.giveDocsUploadLinkResponseLiveData.observe(
                            viewLifecycleOwner
                        ) { it1 ->
                            when (it1) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, it1.data!!.toString())
                                    Log.d(TAG, it1.data.message)
                                    materialAlertDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        it1.data.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        it1.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(TAG, "Give Docs Error ${it1.message}")
                                    hiringViewModel.giveDocsUploadLinkResponseLiveData.removeObservers(
                                        viewLifecycleOwner
                                    )
                                }

                                is NetworkResult.Loading -> {
                                }
                            }
                        }


                    }


                    materialAlertDialog.setOnDismissListener {
                        hiringViewModel.giveDocsUploadLinkResponseLiveData.removeObservers(
                            viewLifecycleOwner
                        )
                    }
                }
            }
            true
        }
        popupMenu.show()

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
                        hiringViewModel.getTemplateContent(templateId, uid)
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
                        hiringViewModel.getTemplateContent(templateId, uid)
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
                    }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
                )

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
                        hiringViewModel.getTemplateContent(templateId, uid)
                    } else sendSmsLayoutBinding.message.text = null

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

    }

    private fun getHiringData() {
        //val dataRequest = DataRequest("hiring")
        hiringViewModel.getHiringData("hiring", pageNo = 1)
    }

    private fun makeCall(uid: Int, hireID: Int, userName: String) {
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
            hiringViewModel.makeCall(callRequest)
        }

        materialAlertDialog.setCanceledOnTouchOutside(false)
        makeCallLayoutBinding.close.setOnClickListener {
            materialAlertDialog.dismiss()
        }
        makeCallLayoutBinding.submit.setOnClickListener {
            val smsRequest = SmsRequest(
                "H",
                "CM",
                null,
                makeCallLayoutBinding.followUpDate.text.toString(),
                hireID,
                makeCallLayoutBinding.remarks.text.toString(),
                statusMap[makeCallLayoutBinding.statusSpinner.selectedItem.toString()],
                null,
                makeCallLayoutBinding.time.text.toString(),
                uid.toString()
            )
            if (! validateSubmitCallRequest(smsRequest)) return@setOnClickListener
            hiringViewModel.sendSms(smsRequest)

            materialAlertDialog.dismiss()
        }
        hiringViewModel.getCrmContact()

    }

    private fun sendCloudMessage(uid: Int, hireID: Int, userName: String) {
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
                "H",
                "SM",
                sendSmsLayoutBinding.message.text.toString(),
                sendSmsLayoutBinding.followUpDate.text.toString(),
                hireID,
                sendSmsLayoutBinding.remarks.text.toString(),
                statusMap[sendSmsLayoutBinding.statusSpinner.selectedItem],
                templateId.toString(),
                sendSmsLayoutBinding.time.text.toString(),
                uid.toString()
            )
            if(! validateSendSmsOrWhatsAppRequest(smsRequest)) return@setOnClickListener
            Log.d(TAG, smsRequest.toString())
            hiringViewModel.sendSms(smsRequest)

            materialAlertDialog.dismiss()
        }
        sendSmsLayoutBinding.close.setOnClickListener {
            materialAlertDialog.dismiss()
        }
        sendSmsLayoutBinding.cancelButton.setOnClickListener {
            materialAlertDialog.dismiss()
        }
        materialAlertDialog.show()
        hiringViewModel.getCrmContact()


    }

    private fun sendWhatsappMessage(uid: Int, hireID: Int, userName: String) {
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
                "H",
                "WM",
                sendSmsLayoutBinding.variableList.text.toString(),
                sendSmsLayoutBinding.followUpDate.text.toString(),
                hireID,
                sendSmsLayoutBinding.remarks.text.toString(),
                statusMap[sendSmsLayoutBinding.statusSpinner.selectedItem],
                templateId.toString(),
                sendSmsLayoutBinding.time.text.toString(),
                uid.toString()
            )
            if( ! validateSendSmsOrWhatsAppRequest(smsRequest)) return@setOnClickListener
            Log.d(TAG, smsRequest.toString())
            hiringViewModel.sendSms(smsRequest)

            materialAlertDialog.dismiss()
        }
        sendSmsLayoutBinding.close.setOnClickListener {
            materialAlertDialog.dismiss()
        }
        sendSmsLayoutBinding.cancelButton.setOnClickListener {
            materialAlertDialog.dismiss()
        }
        materialAlertDialog.show()
        hiringViewModel.getCrmContact()

    }

    private fun sendEmailMessage(uid: Int, hireID: Int, userName: String) {
        this.uid = uid
        this.hireID = hireID
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
                "H",
                "EM",
                templateHtmlText,
                if(sendEmailLayoutBinding.addFooterFlag.isChecked) "true" else "false",
                sendEmailLayoutBinding.followUpDate.text.toString(),
                hireID,
                sendEmailLayoutBinding.remarks.text.toString(),
                sendEmailLayoutBinding.smtpSpinner.selectedItem.toString(),
                statusMap[sendEmailLayoutBinding.statusSpinner.selectedItem],
                sendEmailLayoutBinding.subject.text.toString(),
                templateId.toString(),
                sendEmailLayoutBinding.time.text.toString(),
                uid.toString()
            )
            if( ! validateSendEmailRequest(sendEmailRequest)) return@setOnClickListener
            Log.d(TAG, sendEmailRequest.toString())
            hiringViewModel.sendEmailMessage(sendEmailRequest)
            materialAlertDialog.dismiss()
        }
        materialAlertDialog.show()
        hiringViewModel.getCrmContact()

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

    private fun validateSubmitCallRequest(request: SmsRequest) : Boolean{
        val dialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)

        if(request.remark.isEmpty()){
            dialog.contentText = "Please Write Some Remark"
            dialog.show()
            return false
        }else if(request.date.isEmpty()){
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