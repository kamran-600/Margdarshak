package com.margdarshakendra.margdarshak.interview_fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.margdarshakendra.margdarshak.DashboardActivity
import com.margdarshakendra.margdarshak.databinding.FragmentScheduleReminderBinding
import com.margdarshakendra.margdarshak.models.ScheduleNotificationRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.ScheduleNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleReminderFragment : Fragment() {

    private lateinit var binding: FragmentScheduleReminderBinding
    private val scheduleNotificationViewModel by viewModels<ScheduleNotificationViewModel>()

    private var selectedDate = ""
    private var selectedTime = ""
    @Inject
    lateinit var sharedPreference:SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleReminderBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /**Select DateTime*/

        selectScheduleDateAndTime()

        binding.scheduleBtn.setOnClickListener {
            if (!validateDetails()) return@setOnClickListener

            val scheduleNotificationRequest = ScheduleNotificationRequest(
                selectedDate,
                binding.reminder.text.toString().toInt(),
                binding.reminderCount.text.toString().toInt(),
                binding.task.text.toString(),
                binding.taskName.text.toString(),
                selectedTime
            )
            scheduleNotificationViewModel.scheduleNotification(scheduleNotificationRequest)
        }

        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.setCanceledOnTouchOutside(false)
        sweetAlertDialog.contentText = "Please Wait..."
        scheduleNotificationViewModel.scheduleNotificationLiveData.observe(viewLifecycleOwner){
            sweetAlertDialog.dismiss()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data!!.toString())
                    Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                    successDialogAndRedirectToHome(it.data.message)
                }

                is NetworkResult.Error -> {
                    Log.d(Constants.TAG, it.message.toString())
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    sweetAlertDialog.show()
                }
            }
        }

    }

    private fun validateDetails(): Boolean {
        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
        if (binding.taskName.text.isNullOrEmpty()) {
            sweetAlertDialog.contentText = "Please Fill Task Name"
            sweetAlertDialog.show()
            return false
        }
        if (binding.task.text.isNullOrEmpty()) {
            sweetAlertDialog.contentText = "Please Fill Task"
            sweetAlertDialog.show()
            return false
        }
        if (binding.reminder.text.isNullOrEmpty()) {
            sweetAlertDialog.contentText = "Please Fill Reminder Interval"
            sweetAlertDialog.show()
            return false
        }
        if(binding.reminder.text.toString().toInt() <= 0){
            sweetAlertDialog.contentText = "Please Fill Positive Reminder Interval"
            sweetAlertDialog.show()
            return false
        }
        if (binding.reminderCount.text.isNullOrEmpty()) {
            sweetAlertDialog.contentText = "Please Fill Reminder Count"
            sweetAlertDialog.show()
            return false
        }
        if(binding.reminderCount.text.toString().toInt() <= 0){
            sweetAlertDialog.contentText = "Please Fill Positive Reminder Count"
            sweetAlertDialog.show()
            return false
        }
        if (selectedDate.isEmpty()) {
            sweetAlertDialog.contentText = "Please Select Schedule Date"
            sweetAlertDialog.show()
            return false
        }
        if (selectedTime.isEmpty()) {
            sweetAlertDialog.contentText = "Please Select Schedule Time"
            sweetAlertDialog.show()
            return false
        }

        return true
    }

    private fun selectScheduleDateAndTime() {

        binding.date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    var month1 = month
                    selectedDate = String.format(
                        Locale.getDefault(), "%4d-%02d-%02d", year, ++month1, dayOfMonth
                    )
                    binding.date.setText(selectedDate)
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis

            datePickerDialog.show()
        }

        binding.time.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                requireContext(), { view, hourOfDay, minute ->
                    // val selectedTime = "$hourOfDay:$minute"
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"

                    val compareResponse = compareTime(String.format("%02d:%02d", hourOfDay, minute))
                    if(compareResponse == "true"){
                        binding.time.setText(selectedTime)
                    }
                    else {
                        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialog.contentText = compareResponse
                        sweetAlertDialog.show()
                        binding.time.text = null
                        selectedTime = ""
                    }


                }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
            )
            timePickerDialog.show()
        }

    }

    private fun successDialogAndRedirectToHome(message: String) {
        val successDialog =
            SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
        successDialog.contentText = message
        successDialog.confirmText = "OK"
        successDialog.show()
        /*val home =
            if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {
                StudentHomeFragment()
            } else {
                HomeFragment()
            }*/
        successDialog.setOnDismissListener {
            startActivity(Intent(requireContext(), DashboardActivity::class.java))
            requireActivity().finishAffinity()
            /*requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.bReplace, home)
                .commit()
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(
                R.id.home
            ).isChecked = true*/
        }
    }

    private fun getCurrentFormattedTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = System.currentTimeMillis()
        return dateFormat.format(currentTime)
    }

    private fun compareTime(time1: String): String{

        if(binding.reminder.text?.isEmpty() == true){
            return "Please Fill Reminder Interval First"
        }
        if( binding.reminderCount.text?.isEmpty() == true){
            return "Please Fill Reminder Count First"
        }
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
         try {
            val date1 : Date = dateFormat.parse(time1)!!
            val date2 = dateFormat.parse(getCurrentFormattedTime())!!

            val calendar1 = Calendar.getInstance()
            calendar1.time = date1
            val calendar2 = Calendar.getInstance()
            calendar2.time = date2

             val totalScheduleTime = (binding.reminder.text.toString().toInt())*( binding.reminderCount.text.toString().toInt())
            calendar1.add(Calendar.MINUTE, (-1)*totalScheduleTime)
            if(calendar1.timeInMillis >= calendar2.timeInMillis){
                return "true"
            }
             return "Schedule Time and Current Time Difference should be\nminimum $totalScheduleTime minutes"

        } catch (e: ParseException) {
            return "${e.message}"
        }
    }
}