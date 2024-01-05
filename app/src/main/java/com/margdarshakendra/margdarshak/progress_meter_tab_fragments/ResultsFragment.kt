package com.margdarshakendra.margdarshak.progress_meter_tab_fragments

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.faltenreich.skeletonlayout.applySkeleton
import com.margdarshakendra.margdarshak.adapters.ResultAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentResultsBinding
import com.margdarshakendra.margdarshak.models.GetOrganiserLessonsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.McqResultsRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.ResultsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultsFragment : Fragment() {

    private lateinit var binding: FragmentResultsBinding
    private val resultsViewModel by viewModels<ResultsViewModel>()
    private var courseId = 0

    private lateinit var lessonMap : HashMap<String, Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultsBinding.inflate(inflater, container, false)

        getResultCourses()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lessonMap = HashMap()

        resultsViewModel.resultCoursesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data.toString())
                    val courseMap = LinkedHashMap<String, Int>()

                    for (i in it.data!!.interactiveCourses) {
                        courseMap[i.course] = i.courseID
                    }
                    val coursesListAdapter = ArrayAdapter(
                        requireContext(), R.layout.simple_spinner_dropdown_item,
                        courseMap.keys.toList()
                    )
                    coursesListAdapter.setDropDownViewResource(com.margdarshakendra.margdarshak.R.layout.spinner_dropdown_item)
                    binding.courseAutoCompleteTextView.setAdapter(coursesListAdapter)

                    binding.courseAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        courseId =
                            courseMap[binding.courseAutoCompleteTextView.text.toString()]!!
                        binding.subjectAutoCompleteTextView.setAdapter(null)
                        binding.subjectAutoCompleteTextView.text = null
                        binding.lessonAutoCompleteTextView.setAdapter(null)
                        binding.lessonAutoCompleteTextView.text = null
                        binding.resultRecView.adapter = null
                        getResultSubjects(courseId)
                        Log.d(Constants.TAG, courseId.toString())
                    }
                    binding.courseAutoCompleteTextView.setOnClickListener {
                        binding.courseAutoCompleteTextView.showDropDown()
                    }
                    binding.courseAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.courseAutoCompleteTextView.showDropDown()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        resultsViewModel.resultSubjectsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data.toString())

                    val subjectMap = LinkedHashMap<String, Int>()

                    for (i in it.data!!.subjects) {
                        subjectMap[i.subject] = i.subjectID
                    }
                    val subjectsListAdapter = ArrayAdapter(
                        requireContext(), R.layout.simple_spinner_dropdown_item,
                        subjectMap.keys.toList()
                    )
                    subjectsListAdapter.setDropDownViewResource(com.margdarshakendra.margdarshak.R.layout.spinner_dropdown_item)
                    binding.subjectAutoCompleteTextView.setAdapter(subjectsListAdapter)

                    binding.subjectAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        val subjectId =
                            subjectMap[binding.subjectAutoCompleteTextView.text.toString()]!!
                        binding.lessonAutoCompleteTextView.setAdapter(null)
                        binding.lessonAutoCompleteTextView.text = null
                        getResultLessons(subjectId)
                        Log.d(Constants.TAG, subjectId.toString())
                    }

                    binding.subjectAutoCompleteTextView.setOnClickListener {
                        binding.subjectAutoCompleteTextView.showDropDown()
                    }

                    binding.subjectAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.subjectAutoCompleteTextView.showDropDown()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        resultsViewModel.resultLessonsLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(Constants.TAG, it.data.toString())

                    lessonMap.clear()

                    for (i in it.data!!.lessons) {
                        lessonMap[i.lesson] = i.lessonID
                    }
                    val lessonsListAdapter = ArrayAdapter(
                        requireContext(), R.layout.simple_spinner_dropdown_item,
                        lessonMap.keys.toList()
                    )
                    lessonsListAdapter.setDropDownViewResource(com.margdarshakendra.margdarshak.R.layout.spinner_dropdown_item)
                    binding.lessonAutoCompleteTextView.setAdapter(lessonsListAdapter)

                    binding.lessonAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                        val lessonId =
                            lessonMap[binding.lessonAutoCompleteTextView.text.toString()]!!
                        getMcqResults(lessonId)
                        Log.d(Constants.TAG, lessonId.toString())

                    }

                    binding.lessonAutoCompleteTextView.setOnClickListener {
                        binding.lessonAutoCompleteTextView.showDropDown()
                    }

                    binding.lessonAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            binding.lessonAutoCompleteTextView.showDropDown()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        val skeleton = binding.resultRecView.applySkeleton(com.margdarshakendra.margdarshak.R.layout.single_row_result, 3)
        resultsViewModel.mcqResultsLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showOriginal()
                    Log.d(Constants.TAG, it.data!!.toString())

                    val resultAdapter = ResultAdapter()
                    resultAdapter.submitList(it.data.mcq_results)
                    binding.resultRecView.setHasFixedSize(true)
                    binding.resultRecView.adapter = resultAdapter

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    skeleton.showSkeleton()
                }
            }
        }


    }

    private fun getMcqResults(lessonId: Int) {
        resultsViewModel.getMcqResults(McqResultsRequest(lessonId, "mcq_results"))
    }

    private fun getResultSubjects(courseId: Int) {
        resultsViewModel.getResultSubjectsRequest(GetOrganiserSubjectsRequest(courseId.toString(), "interactiveSubjects"))
    }

    private fun getResultCourses() {
        resultsViewModel.getResultCoursesRequest(GetOrganiserUtilRequest("interactiveCourses"))
    }

    private fun getResultLessons(subjectId: Int) {
        resultsViewModel.getResultLessonsRequest(GetOrganiserLessonsRequest(courseId.toString(), "get_lesson", subjectId.toString()))
    }

}