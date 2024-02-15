package com.margdarshakendra.margdarshak.assessment_fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textview.MaterialTextView
import com.margdarshakendra.margdarshak.DashboardActivity
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.StudentHomeFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.databinding.FragmentSkillTestQuestionsBinding
import com.margdarshakendra.margdarshak.models.SaveSkillTestAnswerRequest
import com.margdarshakendra.margdarshak.models.UpdateTimerRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.SkillTestQuestionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SkillTestQuestionsFragment : Fragment() {

    private lateinit var binding: FragmentSkillTestQuestionsBinding

    private lateinit var countDownTimer: CountDownTimer

    private val skillTestQuestionsViewModel by viewModels<SkillTestQuestionsViewModel>()
    private var page_no = 1
    private var mcqID = 0
    private var testID = 0
    private var resultId = 0
    private var ques_count = ""
    private lateinit var hashMap : HashMap<Int, Boolean>

    @Inject
    lateinit var sharedPreference : SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSkillTestQuestionsBinding.inflate(inflater, container, false)



        resultId = arguments?.getInt("result_id")!!
        val mcq_time = arguments?.getString("mcq_time")
        ques_count = arguments?.getString("ques_count").toString()

        hashMap = HashMap()

        for (i in 1..ques_count.toInt()) {
            val textView = MaterialTextView(requireActivity())
            textView.id = i
            hashMap[textView.id] = false
            textView.text = "$i"
            textView.setTypeface(null, Typeface.BOLD)
            textView.setBackgroundResource(R.drawable.page_no_background)

            textView.setTextColor(requireActivity().getColor(R.color.white))
            textView.setPadding(30, 30, 30, 30)

            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.setMargins(12, 12, 12, 12)

            binding.flexBoxLayout.addView(textView, layoutParams)


            textView.setOnClickListener {
                page_no = i
                skillTestQuestionsViewModel.getSkillTestQuestions(resultId, page_no)
            }
        }

        // Set the duration of the timer in milliseconds
        val durationInMillis: Long = TimeUnit.SECONDS.toMillis(mcq_time!!.toLong())
        // Set the interval of the timer in milliseconds (e.g., 1 second)
        val intervalInMillis: Long = 1000
        countDownTimer = object : CountDownTimer(durationInMillis, intervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimer(resultId.toString(), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).toString())
                // Convert milliseconds to minutes and seconds
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(minutes)

                // Update the UI on each tick
                binding.timerText.text =
                    "Time Left - ${String.format(" %02d: %02d", minutes, seconds)}"

            }

            override fun onFinish() {
                binding.timerText.setTextColor(requireContext().getColor(R.color.green))
                binding.timerText.text = "Timer Finished !"
            }

        }
        countDownTimer.start()



        binding.skipBtn.setOnClickListener {
            if (page_no < ques_count.toInt()) {
                skillTestQuestionsViewModel.getSkillTestQuestions(resultId, ++page_no)
            }
        }

        skillTestQuestionsViewModel.getSkillTestQuestions(resultId, page_no = 1)

        return binding.root

    }

    private fun updateTimer(resultId:String, time_rem : String){
        val updateTimerRequest = UpdateTimerRequest(resultId, time_rem)
        skillTestQuestionsViewModel.updateTimer(updateTimerRequest)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var selectedOption : String? = null
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedOption = when(checkedId){
                R.id.option1 -> {
                    Log.d(TAG, "A")
                    "A"
                }
                R.id.option2 -> {
                    Log.d(TAG, "B")
                    "B"
                }
                R.id.option3 -> {
                    Log.d(TAG, "C")
                    "C"
                }
                R.id.option4 -> {
                    Log.d(TAG, "D")
                    "D"
                }
                else -> {
                    null
                }
            }
        }

        binding.nextBtn.setOnClickListener {
            if (page_no < ques_count.toInt()) {

                if(selectedOption != null){
                    val saveSkillTestAnswerRequest = SaveSkillTestAnswerRequest(
                        selectedOption!!, mcqID.toString(), resultId.toString(),"S", testID.toString() )
                    Log.d(TAG,  saveSkillTestAnswerRequest.toString())
                    skillTestQuestionsViewModel.saveSkillTestAnswer(saveSkillTestAnswerRequest)
                }
                else{
                    Toast.makeText(requireContext(), "Please Select Any Option", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                sweetAlertDialog.titleText = "It is last Question, do you wanna Save the question and Finish The Test?"
                sweetAlertDialog.confirmText = "Yes"
                sweetAlertDialog.setConfirmClickListener {
                    val saveSkillTestAnswerRequest = SaveSkillTestAnswerRequest(
                        selectedOption, mcqID.toString(), resultId.toString(),"F", testID.toString() )
                    Log.d(TAG,  saveSkillTestAnswerRequest.toString())
                    skillTestQuestionsViewModel.saveSkillTestAnswer(saveSkillTestAnswerRequest)
                    it.hide()
                }
                sweetAlertDialog.cancelText = "Only Save"
                sweetAlertDialog.setCancelClickListener {
                    val saveSkillTestAnswerRequest = SaveSkillTestAnswerRequest(
                        selectedOption, mcqID.toString(), resultId.toString(),"S", testID.toString() )
                    Log.d(TAG,  saveSkillTestAnswerRequest.toString())
                    skillTestQuestionsViewModel.saveSkillTestAnswer(saveSkillTestAnswerRequest)
                    it.hide()
                }
                sweetAlertDialog.show()
            }
        }

        binding.finishTestBtn.setOnClickListener {

            val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.titleText = "Are You Sure?"
            sweetAlertDialog.confirmText = "Yes"
            sweetAlertDialog.setConfirmClickListener {
                val saveSkillTestAnswerRequest = SaveSkillTestAnswerRequest(
                    selectedOption, mcqID.toString(), resultId.toString(),"F", testID.toString() )
                Log.d(TAG,  saveSkillTestAnswerRequest.toString())
                skillTestQuestionsViewModel.saveSkillTestAnswer(saveSkillTestAnswerRequest)
                it.hide()
            }
            sweetAlertDialog.cancelText = "No"
            sweetAlertDialog.show()


        }

        skillTestQuestionsViewModel.skillTestQuestionResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    binding.radioGroup.clearCheck()

                    binding.questionNo.text = "$page_no".plus(".")

                    mcqID = it.data.data[0].mcqID
                    testID = it.data.data[0].testID

                    binding.question.text = HtmlCompat.fromHtml(
                        it.data.data[0].question,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()
                    binding.option1.text = HtmlCompat.fromHtml(
                        it.data.data[0].option1,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()
                    binding.option2.text = HtmlCompat.fromHtml(
                        it.data.data[0].option2,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()
                    binding.option3.text = HtmlCompat.fromHtml(
                        it.data.data[0].option3,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()
                    binding.option4.text = HtmlCompat.fromHtml(
                        it.data.data[0].option4,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        skillTestQuestionsViewModel.updateTimerResponseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                   Log.d(TAG, it.data.toString())

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }

        skillTestQuestionsViewModel.saveSkillTestAnswerResponseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    hashMap[page_no] = true

                    val textView = binding.flexBoxLayout.findViewById<MaterialTextView>(page_no)
                    if (hashMap[page_no] == true){
                        textView.setBackgroundResource(R.drawable.page_no_green_background)
                    }

                    if(it.data.completed){
                        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                        sweetAlertDialog.titleText = "You have finished the test"
                        sweetAlertDialog.confirmText = "OK"
                        val home = if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {
                            StudentHomeFragment()
                        } else {
                            HomeFragment()
                        }
                        sweetAlertDialog.setOnDismissListener {
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.bReplace, home)
                                .commit()
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(R.id.home).isChecked = true
                        }
                        sweetAlertDialog.show()
                    }
                    else{
                        if(page_no < ques_count.toInt()) {
                            skillTestQuestionsViewModel.getSkillTestQuestions(result_id = resultId, ++page_no)
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
        }

    }

}