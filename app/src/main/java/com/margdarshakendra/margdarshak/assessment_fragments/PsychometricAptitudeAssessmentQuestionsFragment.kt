package com.margdarshakendra.margdarshak.assessment_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.adapters.AptitudeQuestionsAdapter
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.databinding.FragmentPsychometricAptitudeAssessmentQuestionsBinding
import com.margdarshakendra.margdarshak.models.SaveAptitudeAnswerRequest
import com.margdarshakendra.margdarshak.utils.Constants.QUESTIONPAGENO
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.PsychometricAptitudeAssessmentQuestionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PsychometricAptitudeAssessmentQuestionsFragment : Fragment(),
    AptitudeQuestionsAdapter.AdapterCallback {

    lateinit var binding: FragmentPsychometricAptitudeAssessmentQuestionsBinding

    private var result_id: Int = 0
    private var page_no: Int = 0
    private lateinit var saveAptitudeAnswerRequest: SaveAptitudeAnswerRequest

    @Inject
    lateinit var sharedPreference: SharedPreference

    private val psychometricAptitudeAssessmentQuestionsViewModel by viewModels<PsychometricAptitudeAssessmentQuestionsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPsychometricAptitudeAssessmentQuestionsBinding.inflate(
            inflater,
            container,
            false
        )

        result_id = arguments?.getInt("result_id")!!
         if(sharedPreference.getDetail(QUESTIONPAGENO, "Int") != -1 ){
             page_no = sharedPreference.getDetail(QUESTIONPAGENO, "Int") as Int
             getQuestions(result_id, page_no)
         }else getQuestions(result_id, page_no)


        saveAptitudeAnswerRequest = SaveAptitudeAnswerRequest()
        clickNextBtn()
        clickPrevBtn()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val skeleton: Skeleton =
            binding.questionsRecyclerView.applySkeleton(R.layout.single_row_question, 3)
        skeleton.showSkeleton()
        psychometricAptitudeAssessmentQuestionsViewModel.questionsDataResponseLiveData.observe(
            requireActivity()
        ) {
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showOriginal()
                    Log.d(TAG, it.data!!.toString())
                    Log.d(TAG, page_no.toString())

                    sharedPreference.saveDetail(QUESTIONPAGENO, page_no, "Int")

                    val aptitudeAdapter = AptitudeQuestionsAdapter()
                    aptitudeAdapter.setAdapterCallback(this)
                    aptitudeAdapter.submitList(it.data.questions)
                    binding.questionsRecyclerView.setHasFixedSize(true)
                    binding.questionsRecyclerView.adapter = aptitudeAdapter
                    //  startActivity(Intent(this, DashboardActivity::class.java))
                    // finishAffinity()
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }


        psychometricAptitudeAssessmentQuestionsViewModel.saveAptitudeAnswerResponseLiveData.observe(
            requireActivity()
        ) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(
                        requireContext(),
                        "saved page $page_no response",
                        Toast.LENGTH_SHORT
                    ).show()

                    if(page_no == 8){
                        binding.nextBtn.text = "Finish"
                    }
                    if(it.data.completed){
                        sharedPreference.deleteDetail(QUESTIONPAGENO)
                        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                        sweetAlertDialog.titleText = "Great Job!"
                        sweetAlertDialog.contentText = "You have completed the test."
                        sweetAlertDialog.show()
                        sweetAlertDialog.confirmText = "Go To Home Page"
                        sweetAlertDialog.contentTextSize = 8
                        sweetAlertDialog.setConfirmClickListener {
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.bReplace, HomeFragment()).commit()
                            sweetAlertDialog.hide()
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


    private fun clickNextBtn() {
        binding.nextBtn.setOnClickListener {

            saveAptitudeAnswerRequest.result_id = result_id.toString()
            saveAptitudeAnswerRequest.page_no = page_no

            Log.d(TAG, saveAptitudeAnswerRequest.toString())

            if (
                saveAptitudeAnswerRequest.`1` != null &&
                saveAptitudeAnswerRequest.`2` != null &&
                saveAptitudeAnswerRequest.`3` != null &&
                saveAptitudeAnswerRequest.`4` != null &&
                saveAptitudeAnswerRequest.`5` != null &&
                saveAptitudeAnswerRequest.`6` != null &&
                saveAptitudeAnswerRequest.`7` != null &&
                saveAptitudeAnswerRequest.`8` != null &&
                saveAptitudeAnswerRequest.`9` != null &&
                saveAptitudeAnswerRequest.`10` != null &&
                saveAptitudeAnswerRequest.`11` != null &&
                saveAptitudeAnswerRequest.`12` != null &&
                saveAptitudeAnswerRequest.`13` != null &&
                saveAptitudeAnswerRequest.`14` != null &&
                saveAptitudeAnswerRequest.`15` != null &&
                saveAptitudeAnswerRequest.`16` != null &&
                saveAptitudeAnswerRequest.`17` != null &&
                saveAptitudeAnswerRequest.`18` != null &&
                saveAptitudeAnswerRequest.`19` != null &&
                saveAptitudeAnswerRequest.`20` != null
            ){
                psychometricAptitudeAssessmentQuestionsViewModel.saveAptitudeAnswer(
                    saveAptitudeAnswerRequest
                )
            }
            else {
                Toast.makeText(requireContext(), "Please Attempt Every Question", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }




            if (page_no < 8) {
                getQuestions(result_id, ++page_no)
            }
        }

    }

    private fun clickPrevBtn() {
        binding.prevBtn.setOnClickListener {
            if (page_no > 0) {
                getQuestions(result_id, --page_no)
            } else Toast.makeText(
                requireContext(),
                "This is first page, can't back to previous page",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    private fun getQuestions(result_id: Int, page_no: Int) {

        psychometricAptitudeAssessmentQuestionsViewModel.getAptitudeQuestions(result_id, page_no)

    }

    override fun onRadioButtonSelected(serialNo: Int, selectedRadioBtn: String?) {
        /*val questions = Array(20) { i -> "q${i + 1}" }
        val selectedQuestion = questions.getOrNull(serialNo - 1)

        selectedQuestion?.let {
            saveAptitudeAnswerRequest.apply {
                javaClass.getDeclaredField(it).apply {
                    isAccessible = true
                    set(this@apply, selectedRadioBtn)
                }
            }
        }*/
        Log.d(TAG, "$serialNo, $selectedRadioBtn")
        when (serialNo) {
            1 -> saveAptitudeAnswerRequest.`1` = selectedRadioBtn
            2 -> saveAptitudeAnswerRequest.`2` = selectedRadioBtn
            3 -> saveAptitudeAnswerRequest.`3` = selectedRadioBtn
            4 -> saveAptitudeAnswerRequest.`4` = selectedRadioBtn
            5 -> saveAptitudeAnswerRequest.`5` = selectedRadioBtn
            6 -> saveAptitudeAnswerRequest.`6` = selectedRadioBtn
            7 -> saveAptitudeAnswerRequest.`7` = selectedRadioBtn
            8 -> saveAptitudeAnswerRequest.`8` = selectedRadioBtn
            9 -> saveAptitudeAnswerRequest.`9` = selectedRadioBtn
            10 -> saveAptitudeAnswerRequest.`10` = selectedRadioBtn
            11 -> saveAptitudeAnswerRequest.`11` = selectedRadioBtn
            12 -> saveAptitudeAnswerRequest.`12` = selectedRadioBtn
            13 -> saveAptitudeAnswerRequest.`13` = selectedRadioBtn
            14 -> saveAptitudeAnswerRequest.`14` = selectedRadioBtn
            15 -> saveAptitudeAnswerRequest.`15` = selectedRadioBtn
            16 -> saveAptitudeAnswerRequest.`16` = selectedRadioBtn
            17 -> saveAptitudeAnswerRequest.`17` = selectedRadioBtn
            18 -> saveAptitudeAnswerRequest.`18` = selectedRadioBtn
            19 -> saveAptitudeAnswerRequest.`19` = selectedRadioBtn
            20 -> saveAptitudeAnswerRequest.`20` = selectedRadioBtn
        }
    }

}