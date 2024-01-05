package com.margdarshakendra.margdarshak.assessment_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.StudentHomeFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.databinding.FragmentSkillTestBinding
import com.margdarshakendra.margdarshak.models.SkillResponse
import com.margdarshakendra.margdarshak.models.StartSkillTestRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.SkillTestFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SkillTestFragment : Fragment() {
    private lateinit var binding: FragmentSkillTestBinding

    private val skillTestFragmentViewModel by viewModels<SkillTestFragmentViewModel>()

    @Inject
    lateinit var sharedPreference : SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSkillTestBinding.inflate(inflater, container, false)

        skillTestFragmentViewModel.getSkills()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skillTestFragmentViewModel.skillsResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data.toString())

                    val skillMap = LinkedHashMap<String, SkillResponse.Allskill>()

                    for (i in it.data!!.allskills) {
                        skillMap[i.skill] = i
                    }
                    populateSkillSpinner(skillMap)

                    binding.beginTestBtn.setOnClickListener {
                        if (binding.skillSpinner.selectedItemPosition != 0 && skillMap.size >= 1) {
                            val startSkillTestRequest =
                                StartSkillTestRequest(skillMap[binding.skillSpinner.selectedItem]!!.skills_id)
                            skillTestFragmentViewModel.getStartSkillTest(startSkillTestRequest)
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

        skillTestFragmentViewModel.startSkillTestResponseLiveData.observe(viewLifecycleOwner) {
            binding.spinKit.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())

                    if(it.data.result_id != 0 ){
                        val bundle = Bundle()
                        bundle.putInt("result_id", it.data.result_id)
                        bundle.putString("ques_count", it.data.ques_count)
                        bundle.putString("mcq_time", it.data.mcq_time)

                        val skillTestQuestionsFragment = SkillTestQuestionsFragment()
                        skillTestQuestionsFragment.arguments = bundle

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.bReplace, skillTestQuestionsFragment)
                            .commit()
                    }
                    else {
                        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        sweetAlertDialog.titleText = "You have Already attempted this assessment !"
                        sweetAlertDialog.confirmText = "OK"

                        val home = if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {
                            StudentHomeFragment()
                        } else {
                            HomeFragment()
                        }
                        sweetAlertDialog.setConfirmClickListener {dialog ->
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.bReplace, home)
                                .commit()
                            dialog.hide()
                        }
                        sweetAlertDialog.show()
                    }
                }

                is NetworkResult.Error -> {
                    binding.beginTestBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    binding.spinKit.visibility = View.VISIBLE
                    binding.beginTestBtn.visibility = View.INVISIBLE
                }
            }
        }
    }


    private fun populateSkillSpinner(skillMap: LinkedHashMap<String, SkillResponse.Allskill>) {

        val skillList = skillMap.keys.toMutableList()
        skillList.add(0, "Select Skill")
        val skillAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item, skillList.toList())
        skillAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.skillSpinner.adapter = skillAdapter

        binding.skillSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    binding.questionCount.setText("")
                    binding.time.setText("")
                    binding.totalMarks.setText("")
                    binding.negativeMarks.setText("")
                } else {
                    binding.questionCount.setText(skillMap[binding.skillSpinner.selectedItem]?.quest_count)
                    binding.time.setText(skillMap[binding.skillSpinner.selectedItem]?.time)
                    binding.totalMarks.setText(skillMap[binding.skillSpinner.selectedItem]?.total_marks)
                    binding.negativeMarks.setText(skillMap[binding.skillSpinner.selectedItem]?.neg_marks)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


    }

}