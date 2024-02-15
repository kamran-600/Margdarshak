package com.margdarshakendra.margdarshak.assessment_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.FragmentWorkAttitudeAssessmentBinding
import com.margdarshakendra.margdarshak.models.StartAssessmentRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.WorkAttitudeAssessmentFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkAttitudeAssessmentFragment : Fragment() {

    private lateinit var binding : FragmentWorkAttitudeAssessmentBinding

    private val workAttitudeAssessmentFragmentViewModel by viewModels<WorkAttitudeAssessmentFragmentViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWorkAttitudeAssessmentBinding.inflate(inflater, container, false)

        binding.startFreeBtn.setOnClickListener {
            getStartAttitudeAssessment()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workAttitudeAssessmentFragmentViewModel.startAttitudeAssessmentResponseLiveData.observe(viewLifecycleOwner){
            binding.spinKit.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data!!.toString())

                    val resultIDBundle = Bundle().apply {
                        putInt("result_id", it.data.result_id)
                    }
                    val workAttitudeAssessmentQuestionsFragment = WorkAttitudeAssessmentQuestionsFragment()
                    workAttitudeAssessmentQuestionsFragment.arguments = resultIDBundle

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, workAttitudeAssessmentQuestionsFragment)
                        .commit()

                }

                is NetworkResult.Error -> {
                    binding.startFreeBtn.visibility = View.VISIBLE
                    if(it.message == "Refrence Pending"){
                        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialog.titleText = "You have Already attempted this assessment !"
                        sweetAlertDialog.confirmText = "OK"
                        sweetAlertDialog.show()
                    }
                    else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    binding.spinKit.visibility = View.VISIBLE
                    binding.startFreeBtn.visibility = View.INVISIBLE
                }
            }
        }

    }

    private fun getStartAttitudeAssessment(){
        val startAttitudeAssessmentRequest = StartAssessmentRequest("F") // F for free and P for Paid
        workAttitudeAssessmentFragmentViewModel.getStartAttitudeAssessment(startAttitudeAssessmentRequest)
    }
}