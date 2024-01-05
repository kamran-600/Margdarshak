package com.margdarshakendra.margdarshak.progress_meter_tab_fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.margdarshakendra.margdarshak.PdfViewerActivity
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.VideoViewActivity
import com.margdarshakendra.margdarshak.adapters.InteractiveClassAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentInteractiveClassBinding
import com.margdarshakendra.margdarshak.models.GetOrganiserSubjectsRequest
import com.margdarshakendra.margdarshak.models.GetOrganiserUtilRequest
import com.margdarshakendra.margdarshak.models.InteractiveTeachersRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.InteractiveClassViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InteractiveClassFragment : Fragment() {

    private lateinit var binding: FragmentInteractiveClassBinding
    private val interactiveClassViewModel by viewModels<InteractiveClassViewModel>()
    private var courseId = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInteractiveClassBinding.inflate(inflater, container, false)

        getInteractiveCourses()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        interactiveClassViewModel.interactiveCoursesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())
                    val courseMap = LinkedHashMap<String, Int>()

                    for (i in it.data!!.interactiveCourses) {
                        courseMap[i.course] = i.courseID
                    }
                    val coursesListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        courseMap.keys.toList()
                    )
                    coursesListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.courseAutoCompleteTextView.setAdapter(coursesListAdapter)

                    binding.courseAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        courseId =
                            courseMap[binding.courseAutoCompleteTextView.text.toString()]!!
                        binding.subjectAutoCompleteTextView.setAdapter(null)
                        binding.subjectAutoCompleteTextView.text = null
                        getInteractiveSubjects(courseId)
                        Log.d(TAG, courseId.toString())
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
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }

        interactiveClassViewModel.interactiveSubjectsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())

                    val subjectMap = LinkedHashMap<String, Int>()

                    for (i in it.data!!.subjects) {
                        subjectMap[i.subject] = i.subjectID
                    }
                    val subjectsListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        subjectMap.keys.toList()
                    )
                    subjectsListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    binding.subjectAutoCompleteTextView.setAdapter(subjectsListAdapter)

                    binding.subjectAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        val subjectId =
                            subjectMap[binding.subjectAutoCompleteTextView.text.toString()]!!
                        getInteractiveTeachers(subjectId)
                        Log.d(TAG, subjectId.toString())
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
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {}
            }
        }


        val skeleton: Skeleton =
            binding.interactiveTeachersRecView.applySkeleton(R.layout.single_row_teacher_desc, 3)
        interactiveClassViewModel.interactiveTeachersLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showOriginal()
                    Log.d(TAG, it.data!!.toString())

                    val interactiveClassAdapter = InteractiveClassAdapter(::openPdf, ::goToInteractiveTestFragment, ::openVideo)
                    interactiveClassAdapter.submitList(it.data.data.table_data)
                    binding.interactiveTeachersRecView.setHasFixedSize(true)
                    binding.interactiveTeachersRecView.adapter = interactiveClassAdapter

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {skeleton.showSkeleton()}
            }
        }

    }

    private fun goToInteractiveTestFragment(lessonId : Int){
        val interactiveLessonTestFragment = InteractiveLessonTestFragment()
        val bundle = Bundle()
        bundle.putInt("lessonId", lessonId)
        interactiveLessonTestFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.bReplace,  interactiveLessonTestFragment).addToBackStack(null).commit()
    }

    private fun openPdf(pdfUrl: String?) {
        if(pdfUrl.isNullOrEmpty()){
            Toast.makeText(requireContext(), "Pdf file is null", Toast.LENGTH_SHORT).show()
            return
        }
        /*val uri: Uri = Uri.parse(pdfUrl)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Handle exceptions (e.g., PDF viewer not installed)
            e.printStackTrace()
        }*/

        val intent = Intent(requireContext(), PdfViewerActivity::class.java)
        intent.putExtra("pdfUrl", pdfUrl)
        startActivity(intent)
    }

    private fun openVideo(videoUrl : String?){
        if(videoUrl.isNullOrEmpty()){
            Toast.makeText(requireContext(), "Video File is null", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(requireContext(), VideoViewActivity::class.java)
        intent.putExtra("videoUrl", videoUrl)
        startActivity(intent)

    }

    private fun getInteractiveTeachers(subjectID: Int) {
        val interactiveTeachersRequest = InteractiveTeachersRequest(courseId.toString(), subjectID.toString())
        Log.d(TAG, interactiveTeachersRequest.toString())
        interactiveClassViewModel.getInteractiveTeachersRequest(interactiveTeachersRequest)
    }

    private fun getInteractiveSubjects(courseId: Int) {
        interactiveClassViewModel.getInteractiveSubjectsRequest(
            GetOrganiserSubjectsRequest(
                courseId.toString(),
                "interactiveSubjects"
            )
        )
    }

    private fun getInteractiveCourses() {
        interactiveClassViewModel.getInteractiveCoursesRequest(GetOrganiserUtilRequest("interactiveCourses"))
    }
}