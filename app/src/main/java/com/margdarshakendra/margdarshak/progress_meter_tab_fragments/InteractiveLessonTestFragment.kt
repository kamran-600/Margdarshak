package com.margdarshakendra.margdarshak.progress_meter_tab_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.databinding.FragmentInteractiveLessonTestBinding
import com.margdarshakendra.margdarshak.models.StartInteractiveTestRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.InteractiveLessonTestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InteractiveLessonTestFragment : Fragment() {

    private val interactiveLessonTestViewModel by viewModels<InteractiveLessonTestViewModel>()

    private lateinit var binding: FragmentInteractiveLessonTestBinding

    private var lessonId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInteractiveLessonTestBinding.inflate(inflater, container, false)

        lessonId = arguments?.getInt("lessonId")!!

        interactiveLessonTestViewModel.getInteractiveTestDetails(lessonId)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.beginTestBtn.setOnClickListener {
            val startInteractiveTestRequest = StartInteractiveTestRequest(10, 6)
            interactiveLessonTestViewModel.startInteractiveTestRequest(lessonId, startInteractiveTestRequest)
            Log.d(TAG, startInteractiveTestRequest.toString())
        }


        interactiveLessonTestViewModel.interactiveTestDetailsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    binding.time.setText(it.data.test_details[0].mcq_time)
                    binding.questionCount.setText(it.data.test_details[0].mcq_question)
                    binding.totalMarks.setText(it.data.test_details[0].marks_total)
                    binding.negativeMarks.setText(it.data.test_details[0].marks_wrong)

                    binding.beginTestBtn.isEnabled = true

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        interactiveLessonTestViewModel.startInteractiveTestLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    val interactiveTestQuestionsFragment = InteractiveTestQuestionsFragment()
                    val bundle = Bundle()
                    bundle.putInt("resultId", it.data.resultID)
                    bundle.putInt("timeAlloted", it.data.timeAlloted)

                    interactiveTestQuestionsFragment.arguments = bundle

                    requireActivity().supportFragmentManager.popBackStack()


                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, interactiveTestQuestionsFragment).addToBackStack(null).commit()

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }


    }


}