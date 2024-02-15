package com.margdarshakendra.margdarshak.interview_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.StudentHomeFragment
import com.margdarshakendra.margdarshak.adapters.HrInterviewQuesAdapter
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.databinding.FragmentHrInterviewBinding
import com.margdarshakendra.margdarshak.models.HrInterviewQuesUtilRequest
import com.margdarshakendra.margdarshak.models.StartHrInterviewRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.HrInterviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HrInterviewFragment : Fragment(), HrInterviewQuesAdapter.AdapterCallback {

    private lateinit var binding: FragmentHrInterviewBinding
    private val hrInterviewViewModel by viewModels<HrInterviewViewModel>()
    private var uid = 0
    private var resultID = 0
    private var clickedSubmitInterviewBtn = false
    private lateinit var anyQuestionRankedHashMap : HashMap<Int, Boolean>

    @Inject
    lateinit var sharedPreference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHrInterviewBinding.inflate(inflater, container, false)

        uid = arguments?.getInt("uid")!!

        hrInterviewViewModel.startHrInterviewRequest(StartHrInterviewRequest(uid))


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        anyQuestionRankedHashMap = HashMap()


        hrInterviewViewModel.startHrInterviewLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    resultID = it.data.result_id
                    getHrInterviewQuestions(resultID)

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                }
            }
        }

        val skeleton =
            binding.hrInterviewQuesRecView.applySkeleton(R.layout.single_row_hr_interview_que, 4)
        hrInterviewViewModel.hrInterviewQuesLiveData.observe(viewLifecycleOwner) {
            skeleton.showOriginal()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    val hrInterviewQuesAdapter = HrInterviewQuesAdapter(
                        requireContext(),
                        ::onAskBtnClick,
                        ::onResetAskBtn,
                        ::saveQuesRank
                    )
                    hrInterviewQuesAdapter.setAdapterCallback(this)
                    hrInterviewQuesAdapter.submitList(it.data.questions)

                    binding.hrInterviewQuesRecView.setHasFixedSize(true)
                    binding.hrInterviewQuesRecView.adapter = hrInterviewQuesAdapter

                    setResultAdapter()


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

        hrInterviewViewModel.hrInterviewQuesUtilsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    if (clickedSubmitInterviewBtn && it.data.data.status) {

                        val sweetAlertDialog =
                            SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                        sweetAlertDialog.titleText =
                            "You have submitted the Interview successfully !"
                        sweetAlertDialog.confirmText = "OK"
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
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(R.id.home).isChecked = true
                        }
                        sweetAlertDialog.show()
                    }

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    if(clickedSubmitInterviewBtn)
                    Toast.makeText(
                        requireContext(),
                        "Please Wait Until Get Status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        binding.submitBtn.setOnClickListener {

            if (resultID == 0) return@setOnClickListener

            if (anyQuestionRankedHashMap.size == 0) {
                val sweetAlertDialog =
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                sweetAlertDialog.titleText = "Please Ask and Rank any Question !"
                sweetAlertDialog.confirmText = "OK"
                sweetAlertDialog.show()
                return@setOnClickListener
            }
            if (binding.remark.text?.trim().isNullOrEmpty()) {
                val sweetAlertDialog =
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                sweetAlertDialog.titleText = "Please Write Remark !"
                sweetAlertDialog.confirmText = "OK"
                sweetAlertDialog.show()
                return@setOnClickListener
            }

            if (binding.resultAutoCompleteTextView.text.isNullOrEmpty()) {
                val sweetAlertDialog =
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                sweetAlertDialog.titleText = "Please Select Result !"
                sweetAlertDialog.confirmText = "OK"
                sweetAlertDialog.show()
                return@setOnClickListener
            }

            val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.titleText = "Are You Sure !"
            sweetAlertDialog.confirmText = "Yes"
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()
                submitInterview(resultID)
            }
            sweetAlertDialog.cancelText = "No"
            sweetAlertDialog.show()
        }


    }

    private fun submitInterview(resultID: Int) {

        // save Ques Rank

        Log.d(TAG, binding.resultAutoCompleteTextView.text.toString())
        val result = binding.resultAutoCompleteTextView.text.toString().toCharArray()[0].toString()

        clickedSubmitInterviewBtn = true

        val hrInterviewQuesUtilRequest = HrInterviewQuesUtilRequest(
            "submitInterview",
            resultID,
            remark = binding.remark.text.toString().trim(),
            result = result
        )
        Log.d(TAG, "submit interview $hrInterviewQuesUtilRequest")
        hrInterviewViewModel.setHrInterviewQuesData(hrInterviewQuesUtilRequest)

    }

    private fun setResultAdapter() {
        val resultsList = listOf("Yes", "No", "Pending")

        val resultAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            resultsList
        )
        resultAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.resultAutoCompleteTextView.setAdapter(resultAdapter)

        binding.resultAutoCompleteTextView.setOnClickListener {
            binding.resultAutoCompleteTextView.showDropDown()
        }
        binding.resultAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) binding.resultAutoCompleteTextView.showDropDown()
        }
    }

    private fun onAskBtnClick(hrquestID: Int) {
        clickedSubmitInterviewBtn = false
        // send ques to interviewee
        val hrInterviewQuesUtilRequest =
            HrInterviewQuesUtilRequest("setQuestion", resultID, hrquestID)
        Log.d(TAG, "Ask Ques to Interviewee $hrInterviewQuesUtilRequest")

        hrInterviewViewModel.setHrInterviewQuesData(hrInterviewQuesUtilRequest)

    }

    private fun onResetAskBtn(hrquestID: Int) {
        clickedSubmitInterviewBtn = false
        // reAsk Ques to Interviewee
        val hrInterviewQuesUtilRequest = HrInterviewQuesUtilRequest("resetAsk", resultID, hrquestID)
        Log.d(TAG, "reAsk Ques to Interviewee $hrInterviewQuesUtilRequest")

        hrInterviewViewModel.setHrInterviewQuesData(hrInterviewQuesUtilRequest)

    }

    private fun saveQuesRank(hrquestID: Int, rank: Int?) {
        clickedSubmitInterviewBtn = false
        // save Ques Rank
        val hrInterviewQuesUtilRequest =
            HrInterviewQuesUtilRequest("setQuesRank", resultID, hrquestID, rank)
        Log.d(TAG, "save Ques Rank to Interviewee $hrInterviewQuesUtilRequest")

        hrInterviewViewModel.setHrInterviewQuesData(hrInterviewQuesUtilRequest)

    }


    private fun getHrInterviewQuestions(resultID: Int) {
        hrInterviewViewModel.getHrInterviewQuestions(resultID)
    }

    override fun onUpdateAnyQuestionRankedMap(hrquestID: Int, status: Boolean) {
        if(anyQuestionRankedHashMap.containsKey(hrquestID) ){
            if( ! status) anyQuestionRankedHashMap.remove(hrquestID)
        }
        else anyQuestionRankedHashMap[hrquestID] = status

        Log.d(TAG, "any question Ranked : $anyQuestionRankedHashMap")
    }

}