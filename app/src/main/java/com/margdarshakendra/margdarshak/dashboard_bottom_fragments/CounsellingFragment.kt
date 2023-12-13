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
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.adapters.CounsellingDataRecAdapter
import com.margdarshakendra.margdarshak.broadcastReceivers.NotificationReceiver
import com.margdarshakendra.margdarshak.databinding.FragmentCounsellingBinding
import com.margdarshakendra.margdarshak.databinding.MakeCallLayoutBinding
import com.margdarshakendra.margdarshak.databinding.SendEmailLayoutBinding
import com.margdarshakendra.margdarshak.databinding.SendSmsLayoutBinding
import com.margdarshakendra.margdarshak.models.CRMResponse
import com.margdarshakendra.margdarshak.models.CallRequest
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.models.SendEmailRequest
import com.margdarshakendra.margdarshak.models.SmsRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.HtmlImageGetter
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.CounsellingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CounsellingFragment : Fragment() {

    lateinit var binding: FragmentCounsellingBinding

    private val counsellingViewModel by viewModels<CounsellingViewModel>()


    private lateinit var materialAlertDialog: AlertDialog


    lateinit var sendSmsLayoutBinding: SendSmsLayoutBinding
    lateinit var sendEmailLayoutBinding: SendEmailLayoutBinding
    private lateinit var makeCallLayoutBinding: MakeCallLayoutBinding


    private var templateId = 0
    private var uid = 0

    private lateinit var templateMap: LinkedHashMap<String, Int>
    private lateinit var statusMap: LinkedHashMap<String, String>

    private var whatsappClicked = false
    private var smsClicked = false
    private var emailClicked = false
    private var callClicked = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCounsellingBinding.inflate(layoutInflater, container, false)

        templateMap = LinkedHashMap()
        statusMap = LinkedHashMap()

        sendSmsLayoutBinding = SendSmsLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        sendEmailLayoutBinding =
            SendEmailLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        makeCallLayoutBinding = MakeCallLayoutBinding.inflate(LayoutInflater.from(requireContext()))


        getCounsellingData()

        val skeleton: Skeleton =
            binding.counsellingDataRecyclerView.applySkeleton(R.layout.single_row_clientdata, 5)
        skeleton.showSkeleton()
        counsellingViewModel.counsellingDataResponseLiveData.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showOriginal()
                    Log.d(Constants.TAG, it.data!!.toString())

                    val counsellingDataRecAdapter = CounsellingDataRecAdapter(
                        requireContext(), ::makeCall,
                        ::sendCloudMessage,
                        ::sendWhatsappMessage,
                        ::sendEmailMessage
                    )
                    counsellingDataRecAdapter.submitList(it.data.data)

                    binding.counsellingDataRecyclerView.setHasFixedSize(true)
                    binding.counsellingDataRecyclerView.adapter = counsellingDataRecAdapter

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        counsellingViewModel.callResponseLiveData.observe(requireActivity()) {

            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data!!.data.toString())
                    Toast.makeText(requireContext(), it.data.data.message, Toast.LENGTH_SHORT)
                        .show()


                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        counsellingViewModel.crmResponseLiveData.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data!!.data.sms.toString())
                    setFollowDate()
                    setTime()
                    populateStatusSpinner(it.data.data.status)

                    if (smsClicked) {
                        Log.d(Constants.TAG, smsClicked.toString())
                        populateTemplateSpinnerSms(it.data.data.sms)
                    } else if (whatsappClicked) {
                        populateTemplateSpinnerWhatsApp(it.data.data.whatsapp)
                    } else if (emailClicked) {
                        populateTemplateSpinnerEmail(it.data.data.email)
                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        counsellingViewModel.smsResponseLiveData.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()

                    val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault())

                    var date = ""

                    if(callClicked){
                        date = "${makeCallLayoutBinding.followUpDate.text} ${makeCallLayoutBinding.time.text}"
                    }
                    else{
                        date = "${sendSmsLayoutBinding.followUpDate.text} ${sendSmsLayoutBinding.time.text}"
                    }

                    var convertedDate: Date? = null
                    try {
                        convertedDate = dateFormat.parse(date)
                    } catch (e: ParseException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }

                    if (convertedDate != null) {
                        Log.d(Constants.TAG, date)
                        Log.d(Constants.TAG, convertedDate.toString())
                        val calendar = Calendar.getInstance()
                        calendar.time = convertedDate
                        calendar.add(Calendar.MINUTE, -10)
                        val timeInMillis = calendar.timeInMillis
                        Log.d(Constants.TAG, calendar.time.toString())
                        scheduleNotification(timeInMillis)

                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        counsellingViewModel.emailSmsResponseLiveData.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                   // scheduleNotification(sendEmailLayoutBinding.followUpDate.text)

                    val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault())

                    val date = "${sendEmailLayoutBinding.followUpDate.text} ${sendEmailLayoutBinding.time.text}"

                    var convertedDate: Date? = null
                    try {
                        convertedDate = dateFormat.parse(date)
                    } catch (e: ParseException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }

                    if (convertedDate != null) {
                        Log.d(Constants.TAG, date)
                        Log.d(Constants.TAG, convertedDate.toString())
                        val calendar = Calendar.getInstance()
                        calendar.time = convertedDate!!
                        calendar.add(Calendar.MINUTE, -10)
                        val timeInMillis = calendar.timeInMillis
                        Log.d(Constants.TAG, calendar.time.toString())
                        scheduleNotification(timeInMillis)
                        Log.d(TAG, "scheduled  notification")

                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        counsellingViewModel.templateResponseLiveData.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data!!.toString())
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

                        sendEmailLayoutBinding.submit.setOnClickListener { _ ->
                            val sendEmailRequest = SendEmailRequest(
                                "C",
                                "EM",
                                it.data.template,
                                sendEmailLayoutBinding.followUpDate.text.toString(),
                                null,
                                sendEmailLayoutBinding.remarks.text.toString(),
                                sendEmailLayoutBinding.smtpSpinner.selectedItem.toString(),
                                statusMap[sendEmailLayoutBinding.statusSpinner.selectedItem]!!,
                                sendEmailLayoutBinding.subject.text.toString(),
                                templateId.toString(),
                                sendEmailLayoutBinding.time.text.toString(),
                                uid.toString()
                            )

                            Log.d(Constants.TAG, sendEmailRequest.toString())
                            Log.d(Constants.TAG, sendEmailLayoutBinding.message.text.toString())
                            counsellingViewModel.sendEmailMessage(sendEmailRequest)
                            materialAlertDialog.hide()
                        }
                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        return binding.root
    }

    private fun getCounsellingData() {
        val dataRequest = DataRequest("counselling")
        counsellingViewModel.getCounsellingData(dataRequest)
    }

    private fun populateTemplateSpinnerEmail(emailTemplates: List<CRMResponse.Data.Email>) {

        templateMap.clear()
        templateMap["Select Template"] = 0
        for (i in emailTemplates) {
            templateMap[i.template] = i.templateID
        }
        Log.d(Constants.TAG, templateMap.toString())
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
        Log.d(Constants.TAG, templateMap.toString())
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setTime() {
        if (callClicked) {
            makeCallLayoutBinding.time.setOnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.action === MotionEvent.ACTION_UP) {
                    if (event.rawX <= makeCallLayoutBinding.time.right + makeCallLayoutBinding.time.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
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
                }
                true
            }
        } else if (smsClicked || whatsappClicked) {
            sendSmsLayoutBinding.time.setOnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.action === MotionEvent.ACTION_UP) {
                    if (event.rawX <= sendSmsLayoutBinding.time.right + sendSmsLayoutBinding.time.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
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
                }
                true
            }
        } else if (emailClicked) {
            sendEmailLayoutBinding.time.setOnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.action === MotionEvent.ACTION_UP) {
                    if (event.rawX <= sendEmailLayoutBinding.time.right + sendEmailLayoutBinding.time.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
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
                true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setFollowDate() {
        if (callClicked) {
            makeCallLayoutBinding.followUpDate.setOnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.action === MotionEvent.ACTION_UP) {
                    if (event.rawX <= makeCallLayoutBinding.followUpDate.right + makeCallLayoutBinding.followUpDate.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
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
                        return@setOnTouchListener true
                    }
                }
                false
            }
        } else if (smsClicked || whatsappClicked) {
            sendSmsLayoutBinding.followUpDate.setOnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.action === MotionEvent.ACTION_UP) {
                    if (event.rawX <= sendSmsLayoutBinding.followUpDate.right + sendSmsLayoutBinding.followUpDate.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
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
                        return@setOnTouchListener true
                    }
                }
                false
            }
        } else if (emailClicked) {
            sendEmailLayoutBinding.followUpDate.setOnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.action === MotionEvent.ACTION_UP) {
                    if (event.rawX <= sendEmailLayoutBinding.followUpDate.right + sendEmailLayoutBinding.followUpDate.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
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
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }

    }

    private fun populateStatusSpinner(status: List<CRMResponse.Data.Status>) {
        for (i in status) {
            statusMap[i.action] = i.status
        }
        Log.d(Constants.TAG, statusMap.toString())

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
        Log.d(Constants.TAG, templateMap.toString())
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
            Log.d(Constants.TAG, callRequest.toString())
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
                statusMap[makeCallLayoutBinding.statusSpinner.selectedItem.toString()]!!,
                null,
                makeCallLayoutBinding.time.text.toString(),
                uid.toString()
            )
            counsellingViewModel.sendSms(smsRequest)


            materialAlertDialog.hide()
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
                statusMap[sendSmsLayoutBinding.statusSpinner.selectedItem]!!,
                templateId.toString(),
                sendSmsLayoutBinding.time.text.toString(),
                uid.toString()
            )
            Log.d(Constants.TAG, smsRequest.toString())
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
                statusMap[sendSmsLayoutBinding.statusSpinner.selectedItem]!!,
                templateId.toString(),
                sendSmsLayoutBinding.time.text.toString(),
                uid.toString()
            )
            Log.d(Constants.TAG, smsRequest.toString())
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
            materialAlertDialog.hide()
        }
        sendEmailLayoutBinding.cancelButton.setOnClickListener {
            materialAlertDialog.hide()
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


    private fun scheduleNotification(timeInMillis: Long) {

        if(Calendar.getInstance().timeInMillis > timeInMillis){
            return
        }

        createNotificationChannel()
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(requireContext(), NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity(), 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms())
                   // Toast.makeText(requireContext(), "true", Toast.LENGTH_SHORT).show()
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)


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


}