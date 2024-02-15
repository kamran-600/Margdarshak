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
import com.faltenreich.skeletonlayout.applySkeleton
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.adapters.AttitudeRatingQueAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentWorkAttitudeRatingPhaseBinding
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesRequest
import com.margdarshakendra.margdarshak.models.SaveAttitudeRatingQuesResponse
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.WorkAttitudeRatingPhaseFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkAttitudeRatingPhaseFragment : Fragment(), AttitudeRatingQueAdapter.AdapterCallback {

    private lateinit var binding: FragmentWorkAttitudeRatingPhaseBinding
    private val workAttitudeRatingPhaseFragmentViewModel by viewModels<WorkAttitudeRatingPhaseFragmentViewModel>()
    private var result_id = "0"
    private lateinit var attitudeRatingQueAdapter: AttitudeRatingQueAdapter

    private lateinit var saveAttitudeRatingQuesRequest: SaveAttitudeRatingQuesRequest
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWorkAttitudeRatingPhaseBinding.inflate(inflater, container, false)

        result_id = arguments?.getString("result_id")!!
        Log.d(TAG, result_id)
        workAttitudeRatingPhaseFragmentViewModel.getAttitudeRatingQuestions(result_id.toInt())

        saveAttitudeRatingQuesRequest = SaveAttitudeRatingQuesRequest()

        clickFinishTestBtn()


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val skeleton = binding.questionsRecyclerView.applySkeleton(
            R.layout.single_row_rating_phase_question,
            4
        )
        skeleton.showSkeleton()
        workAttitudeRatingPhaseFragmentViewModel.attitudeRatingQuestionsDataResponseLiveData.observe(
            viewLifecycleOwner
        ) {
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showOriginal()
                    Log.d(TAG, it.data!!.toString())


                    attitudeRatingQueAdapter = AttitudeRatingQueAdapter()
                    attitudeRatingQueAdapter.setAdapterCallback(this)
                    attitudeRatingQueAdapter.submitList(it.data.questions)
                    binding.questionsRecyclerView.setHasFixedSize(true)
                    binding.questionsRecyclerView.adapter = attitudeRatingQueAdapter


                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }


        workAttitudeRatingPhaseFragmentViewModel.saveAttitudeRatingQuesAndFinishTestResponseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    if(it.data.rating && it.data.completed){
                        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialog.titleText = "You have Passed the Rating Assessment !"
                        sweetAlertDialog.confirmText = "OK"
                        sweetAlertDialog.show()
                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun clickFinishTestBtn(){

        binding.finishTestBtn.setOnClickListener {

            saveAttitudeRatingQuesRequest.type = "F"

            Log.d(TAG, saveAttitudeRatingQuesRequest.toString())

            if (
                saveAttitudeRatingQuesRequest.`1` != null &&
                saveAttitudeRatingQuesRequest.`2` != null &&
                saveAttitudeRatingQuesRequest.`3` != null &&
                saveAttitudeRatingQuesRequest.`4` != null &&
                saveAttitudeRatingQuesRequest.`5` != null &&
                saveAttitudeRatingQuesRequest.`6` != null &&
                saveAttitudeRatingQuesRequest.`7` != null &&
                saveAttitudeRatingQuesRequest.`8` != null &&
                saveAttitudeRatingQuesRequest.`9` != null &&
                saveAttitudeRatingQuesRequest.`10` != null &&
                saveAttitudeRatingQuesRequest.`11` != null &&
                saveAttitudeRatingQuesRequest.`12` != null &&
                saveAttitudeRatingQuesRequest.`13` != null &&
                saveAttitudeRatingQuesRequest.`14` != null &&
                saveAttitudeRatingQuesRequest.`15` != null &&
                saveAttitudeRatingQuesRequest.`16` != null &&
                saveAttitudeRatingQuesRequest.`17` != null &&
                saveAttitudeRatingQuesRequest.`18` != null &&
                saveAttitudeRatingQuesRequest.`19` != null &&
                saveAttitudeRatingQuesRequest.`20` != null &&
                saveAttitudeRatingQuesRequest.`21` != null
            ){
                workAttitudeRatingPhaseFragmentViewModel.saveAttitudeRatingQuesAndFinishTest(
                    result_id.toInt(), saveAttitudeRatingQuesRequest
                )
            }
            else {
                Toast.makeText(requireContext(), "Please Attempt Every Question", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

        }
    }

    override fun onRadioButtonSelected(serialNo: Int, selectedRadioBtn: String?) {
        Log.d(TAG, "$serialNo, $selectedRadioBtn")

        when (serialNo) {
            1 -> saveAttitudeRatingQuesRequest.`1` = selectedRadioBtn
            2 -> saveAttitudeRatingQuesRequest.`2` = selectedRadioBtn
            3 -> saveAttitudeRatingQuesRequest.`3` = selectedRadioBtn
            4 -> saveAttitudeRatingQuesRequest.`4` = selectedRadioBtn
            5 -> saveAttitudeRatingQuesRequest.`5` = selectedRadioBtn
            6 -> saveAttitudeRatingQuesRequest.`6` = selectedRadioBtn
            7 -> saveAttitudeRatingQuesRequest.`7` = selectedRadioBtn
            8 -> saveAttitudeRatingQuesRequest.`8` = selectedRadioBtn
            9 -> saveAttitudeRatingQuesRequest.`9` = selectedRadioBtn
            10 ->saveAttitudeRatingQuesRequest.`10` = selectedRadioBtn
            11 ->saveAttitudeRatingQuesRequest.`11` = selectedRadioBtn
            12 ->saveAttitudeRatingQuesRequest.`12` = selectedRadioBtn
            13 ->saveAttitudeRatingQuesRequest.`13` = selectedRadioBtn
            14 ->saveAttitudeRatingQuesRequest.`14` = selectedRadioBtn
            15 ->saveAttitudeRatingQuesRequest.`15` = selectedRadioBtn
            16 ->saveAttitudeRatingQuesRequest.`16` = selectedRadioBtn
            17 ->saveAttitudeRatingQuesRequest.`17` = selectedRadioBtn
            18 ->saveAttitudeRatingQuesRequest.`18` = selectedRadioBtn
            19 ->saveAttitudeRatingQuesRequest.`19` = selectedRadioBtn
            20 ->saveAttitudeRatingQuesRequest.`20` = selectedRadioBtn
            21 ->saveAttitudeRatingQuesRequest.`21` = selectedRadioBtn
        }
    }

}