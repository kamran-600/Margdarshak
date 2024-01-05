package com.margdarshakendra.margdarshak.progress_meter_tab_fragments

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.textview.MaterialTextView
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.StudentHomeFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.databinding.FragmentInteractiveTestQuestionsBinding
import com.margdarshakendra.margdarshak.models.SaveInteractiveAnswerRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.InteractiveTestQuestionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.noties.jlatexmath.JLatexMathDrawable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class InteractiveTestQuestionsFragment : Fragment() {

    private lateinit var binding: FragmentInteractiveTestQuestionsBinding

    private lateinit var countDownTimer: CountDownTimer

    private val interactiveTestQuestionsViewModel by viewModels<InteractiveTestQuestionsViewModel>()
    private var resultId = 0
    private var timeAlloted = 0
    private var quesNo = 1
    private var mcqID = 0
    private var testID = 0
    private val quesCount = 10
    private lateinit var hashMap: HashMap<Int, Boolean>

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInteractiveTestQuestionsBinding.inflate(inflater, container, false)

        resultId = arguments?.getInt("resultId")!!
        timeAlloted = arguments?.getInt("timeAlloted")!!

        interactiveTestQuestionsViewModel.getInteractiveQuestions(resultId, quesNo)

        hashMap = HashMap()

        for (i in 1..quesCount) {
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
                quesNo = i
                interactiveTestQuestionsViewModel.getInteractiveQuestions(resultId, quesNo)
            }
        }


        val durationInMillis: Long = TimeUnit.SECONDS.toMillis(timeAlloted.toLong())
        val intervalInMillis: Long = 1000
        countDownTimer = object : CountDownTimer(durationInMillis, intervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
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
            if (quesNo < quesCount) {
                interactiveTestQuestionsViewModel.getInteractiveQuestions(resultId, ++quesNo)
            }
        }


        return binding.root
    }

    private fun getRemainingSeconds(): Int {

        // Extract minutes and seconds from the time string
        val regex = Regex("""(\d{2}): (\d{2})""")
        val matchResult = regex.find(binding.timerText.text)

        if (matchResult != null) {
            val (minutes, seconds) = matchResult.destructured

            val totalSeconds = minutes.toInt() * 60 + seconds.toInt()
            Log.d(TAG, ("Total seconds: $totalSeconds"))

            return totalSeconds
        } else {
            Log.d(TAG, ("Timer is finished !"))
            return 0
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var selectedOption: String? = null
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedOption = when (checkedId) {
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
            if (quesNo < quesCount) {

                if (selectedOption != null) {

                    if (getRemainingSeconds() != 0) {
                        val saveInteractiveAnswerRequest = SaveInteractiveAnswerRequest(
                            selectedOption!!, mcqID, resultId, "S", testID, getRemainingSeconds()
                        )
                        Log.d(TAG, saveInteractiveAnswerRequest.toString())
                        interactiveTestQuestionsViewModel.saveInteractiveAnswerRequest(
                            saveInteractiveAnswerRequest
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Timer is Over, Now Please Finish the test",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(requireContext(), "Please Select Any Option", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val sweetAlertDialog =
                    SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                sweetAlertDialog.titleText =
                    "It is last Question, do you wanna Save the question and Finish The Test?"
                sweetAlertDialog.confirmText = "Yes"
                sweetAlertDialog.setConfirmClickListener {
                    if (getRemainingSeconds() != 0) {
                        val saveInteractiveAnswerRequest = SaveInteractiveAnswerRequest(
                            selectedOption!!, mcqID, resultId, "F", testID, getRemainingSeconds()
                        )
                        Log.d(TAG, saveInteractiveAnswerRequest.toString())
                        interactiveTestQuestionsViewModel.saveInteractiveAnswerRequest(
                            saveInteractiveAnswerRequest
                        )
                    } else Toast.makeText(
                        requireContext(),
                        "Timer is Over, Now Please Finish the test",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.hide()
                }
                sweetAlertDialog.cancelText = "Only Save"
                sweetAlertDialog.setCancelClickListener {
                    if (getRemainingSeconds() != 0) {
                        val saveInteractiveAnswerRequest = SaveInteractiveAnswerRequest(
                            selectedOption!!, mcqID, resultId, "S", testID, getRemainingSeconds()
                        )
                        Log.d(TAG, saveInteractiveAnswerRequest.toString())
                        interactiveTestQuestionsViewModel.saveInteractiveAnswerRequest(
                            saveInteractiveAnswerRequest
                        )
                    } else Toast.makeText(
                        requireContext(),
                        "Timer is Over, Now Please Finish the test",
                        Toast.LENGTH_SHORT
                    ).show()
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
                if(selectedOption != null) {
                    val saveInteractiveAnswerRequest = SaveInteractiveAnswerRequest(
                        selectedOption!!, mcqID, resultId, "F", testID, getRemainingSeconds()
                    )
                    Log.d(TAG, saveInteractiveAnswerRequest.toString())
                    interactiveTestQuestionsViewModel.saveInteractiveAnswerRequest(
                        saveInteractiveAnswerRequest
                    )
                }
                else Toast.makeText(requireContext(), "Please Select Any Option", Toast.LENGTH_SHORT).show()
                it.hide()
            }
            sweetAlertDialog.cancelText = "No"
            sweetAlertDialog.show()


        }


        val skeleton = binding.questionLL.createSkeleton()
        skeleton.showSkeleton()
        interactiveTestQuestionsViewModel.interactiveTestQuestionLiveData.observe(viewLifecycleOwner)
        {
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showOriginal()
                    Log.d(TAG, it.data!!.toString())
                    if (it.data.question_data.isEmpty()) {
                        return@observe
                    }

                    binding.radioGroup.clearCheck()

                    binding.questionNo.text = "$quesNo".plus(".")

                    mcqID = it.data.question_data[0].mcqID
                    testID = it.data.question_data[0].testID

                    when (it.data.question_data[0].answer) {
                        "A" -> {
                            binding.option1.isChecked = true
                        }

                        "B" -> {
                            binding.option2.isChecked = true
                        }

                        "C" -> {
                            binding.option3.isChecked = true
                        }

                        "D" -> {
                            binding.option4.isChecked = true
                        }

                        else -> {}
                    }

                    val htmlParsedText = HtmlCompat.fromHtml(
                        it.data.question_data[0].question,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim().toString()
                    Log.d(TAG, htmlParsedText)

                    if(containsLatexExpressions(htmlParsedText)){
                        val drawable = JLatexMathDrawable.builder(htmlParsedText)
                            .textSize(90F)
                            .padding(8)
                            .align(JLatexMathDrawable.ALIGN_RIGHT)
                            .build()


                        binding.latexQuestion.visibility = View.VISIBLE
                        binding.question.visibility = View.GONE

                        binding.latexQuestion.setLatexDrawable(drawable)

                    } else{
                        binding.question.visibility = View.VISIBLE
                        binding.latexQuestion.visibility = View.GONE
                        binding.question.text = htmlParsedText
                    }


                    val htmlParsedOption1 = HtmlCompat.fromHtml(
                        it.data.question_data[0].option1,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim().toString()

                    if (containsLatexExpressions(htmlParsedOption1)) {
                        val option1drawable = JLatexMathDrawable.builder(htmlParsedOption1)
                            .textSize(70F)
                            .padding(8)
                            .align(JLatexMathDrawable.ALIGN_RIGHT)
                            .build()

                        binding.option1.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            option1drawable as Drawable,
                            null
                        )

                        binding.option1.text = null

                    } else {
                        binding.option1.text = htmlParsedOption1
                        binding.option1.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)
                    }


                    val htmlParsedOption2 = HtmlCompat.fromHtml(
                        it.data.question_data[0].option2,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim().toString()

                    if (containsLatexExpressions(htmlParsedOption2)) {
                        val option2drawable = JLatexMathDrawable.builder(htmlParsedOption2)
                            .textSize(70F)
                            .padding(8)
                            .align(JLatexMathDrawable.ALIGN_RIGHT)
                            .build()

                        binding.option2.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            option2drawable as Drawable,
                            null
                        )

                        binding.option2.text = null

                    } else {
                        binding.option2.text = htmlParsedOption2
                        binding.option2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)

                    }


                    val htmlParsedOption3 = HtmlCompat.fromHtml(
                        it.data.question_data[0].option3,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim().toString()

                    if (containsLatexExpressions(htmlParsedOption3)) {
                        val option3drawable = JLatexMathDrawable.builder(htmlParsedOption3)
                            .textSize(70F)
                            .padding(8)
                            .align(JLatexMathDrawable.ALIGN_RIGHT)
                            .build()

                        binding.option3.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            option3drawable as Drawable,
                            null
                        )

                        binding.option3.text = null

                    } else {
                        binding.option3.text = htmlParsedOption3
                        binding.option3.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)

                    }


                    val htmlParsedOption4 = HtmlCompat.fromHtml(
                        it.data.question_data[0].option4,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim().toString()

                    if (containsLatexExpressions(htmlParsedOption4)) {
                        val option4drawable = JLatexMathDrawable.builder(htmlParsedOption4)
                            .textSize(70F)
                            .padding(8)
                            .align(JLatexMathDrawable.ALIGN_RIGHT)
                            .build()

                        binding.option4.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            option4drawable as Drawable,
                            null
                        )

                        binding.option4.text = null

                    } else {
                        binding.option4.text = htmlParsedOption4
                        binding.option4.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)

                    }



/*

                    binding.option2.text = HtmlCompat.fromHtml(
                        it.data.question_data[0].option2,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()
                    binding.option3.text = HtmlCompat.fromHtml(
                        it.data.question_data[0].option3,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()
                    binding.option4.text = HtmlCompat.fromHtml(
                        it.data.question_data[0].option4,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).trim()

*/

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    --quesNo
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        interactiveTestQuestionsViewModel.saveInteractiveAnsResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    hashMap[quesNo] = true

                    val textView = binding.flexBoxLayout.findViewById<MaterialTextView>(quesNo)
                    if (hashMap[quesNo] == true) {
                        textView.setBackgroundResource(R.drawable.page_no_green_background)
                    }

                    if (it.data.message != null) {
                        val sweetAlertDialog =
                            SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                        sweetAlertDialog.titleText = "${it.data.message}"
                        sweetAlertDialog.confirmText = "OK"
                        val home =
                            if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {
                                StudentHomeFragment()
                            } else {
                                HomeFragment()
                            }
                        sweetAlertDialog.setOnDismissListener { dialog ->
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.bReplace, home)
                                .commit()
                        }
                        sweetAlertDialog.show()
                    } else {
                        if (quesNo < quesCount) {
                            interactiveTestQuestionsViewModel.getInteractiveQuestions(
                                resultId,
                                ++quesNo
                            )
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


    private fun containsLatexExpressions(input: String): Boolean {
        // val latexRegex = Regex("\\(.*?\\)")
         return input.startsWith("\\(") && input.endsWith("\\)")
       // val latexRegex = Regex("""^\\(.*\\)$""")
       // val latexRegex = Regex("""<p><span class="math-tex">\\(.*?\\)</span></p>""")
/*
        val matcher = latexRegex.find(input)
        return matcher != null*/
    }

}