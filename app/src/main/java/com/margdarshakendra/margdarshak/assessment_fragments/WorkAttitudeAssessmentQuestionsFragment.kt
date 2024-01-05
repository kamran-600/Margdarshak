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
import com.margdarshakendra.margdarshak.adapters.AttitudeQuestionsAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentWorkAttitudeAssessmentQuestionsBinding
import com.margdarshakendra.margdarshak.models.SaveAttitudeQuestionsRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.WorkAttitudeAssessmentQuestionsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WorkAttitudeAssessmentQuestionsFragment : Fragment(),
    AttitudeQuestionsAdapter.AdapterCallback {

    private lateinit var binding: FragmentWorkAttitudeAssessmentQuestionsBinding

    private val workAttitudeAssessmentQuestionsFragmentViewModel by viewModels<WorkAttitudeAssessmentQuestionsFragmentViewModel>()

    @Inject
    lateinit var sharedPreference: SharedPreference

    private var page_no = 1
    private var prev_opened_page = -1
    private var result_id = 0

    lateinit var saveAttitudeQuestionsRequest: SaveAttitudeQuestionsRequest

    private lateinit var attiquestPrefList: ArrayList<String>
    // private var updatedPosition = 0

    private lateinit var attitudeAdapter: AttitudeQuestionsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWorkAttitudeAssessmentQuestionsBinding.inflate(inflater, container, false)

        result_id = arguments?.getInt("result_id")!!
        /*if(sharedPreference.getDetail(Constants.ATTITUDEQUESTIONPAGENO, "Int") != -1 ){
            page_no = sharedPreference.getDetail(Constants.ATTITUDEQUESTIONPAGENO, "Int") as Int
            getAttitudeQuestions(page_no)
        }else if((sharedPreference.getDetail(Constants.ATTITUDEASSESSMENTCOMPLETED, "Boolean") as Boolean) && ! (sharedPreference.getDetail(Constants.ATTITUDERATINGCOMPLETED, "Boolean") as Boolean) && !(sharedPreference.getDetail(Constants.ATTITUDECOMPLETED, "Boolean") as Boolean)){
            Toast.makeText(requireContext(), "You have completed Assessment and now go for rating phase", Toast.LENGTH_SHORT).show()

            val resultIDBundle = Bundle().apply {
                putString("result_id", result_id.toString())
            }
            val workAttitudeRatingPhaseFragment = WorkAttitudeRatingPhaseFragment()
            workAttitudeRatingPhaseFragment.arguments = resultIDBundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.bReplace, workAttitudeRatingPhaseFragment)
                .commit()
        }else if((sharedPreference.getDetail(Constants.ATTITUDEASSESSMENTCOMPLETED, "Boolean") as Boolean) && (sharedPreference.getDetail(Constants.ATTITUDERATINGCOMPLETED, "Boolean") as Boolean) && !(sharedPreference.getDetail(Constants.ATTITUDECOMPLETED, "Boolean") as Boolean)){

        }*/

        getAttitudeQuestions(page_no)

        attiquestPrefList = ArrayList()

        clickNextBtn()
        clickPrevBtn()
        clickDeselectAll()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val skeleton = binding.attitudeQuestionsRecyclerView.applySkeleton(
            R.layout.single_row_attitude_question,
            5
        )
        skeleton.showSkeleton()
        workAttitudeAssessmentQuestionsFragmentViewModel.attitudeQuestionsDataResponseLiveData.observe(
            viewLifecycleOwner
        ) {
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showOriginal()

                    attiquestPrefList.clear()
                    Log.d(TAG, it.data!!.toString())
                    Log.d(TAG, page_no.toString())

                    binding.pageNo.text = "Page $page_no of 21"

                   // sharedPreference.saveDetail(Constants.ATTITUDEQUESTIONPAGENO, page_no, "Int")

                    attitudeAdapter = AttitudeQuestionsAdapter(requireActivity())
                    attitudeAdapter.setAdapterCallback(this)
                    attitudeAdapter.submitList(it.data.data)
                    binding.attitudeQuestionsRecyclerView.setHasFixedSize(true)
                    binding.attitudeQuestionsRecyclerView.adapter = attitudeAdapter

                    Log.d(TAG, "$prev_opened_page , $page_no")

                    if(prev_opened_page - page_no == 1 ){
                        for (item in attitudeAdapter.currentList) {
                            item.isSelected = true
                        }
                        attitudeAdapter.notifyItemRangeChanged(0, attitudeAdapter.itemCount)
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


        workAttitudeAssessmentQuestionsFragmentViewModel.saveAttitudeQuestionsResponseLiveData.observe(viewLifecycleOwner){
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    Toast.makeText(
                        requireContext(),
                        "saved page $page_no response",
                        Toast.LENGTH_SHORT
                    ).show()

                    if(page_no <21){
                        getAttitudeQuestions(++page_no)
                    }
                    if(it.data.assessment && !it.data.completed){
                      //  sharedPreference.deleteDetail(Constants.ATTITUDEQUESTIONPAGENO)
                      //  sharedPreference.saveDetail(Constants.ATTITUDEASSESSMENTCOMPLETED, it.data.assessment, "Boolean")
                      //  sharedPreference.saveDetail(Constants.ATTITUDECOMPLETED, it.data.completed, "Boolean")

                        /*//
                        val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                        sweetAlertDialog.titleText = "Great Job!"
                        sweetAlertDialog.contentText = "You have completed Assessment."
                        sweetAlertDialog.show()
                        sweetAlertDialog.confirmText = "Go To Home Page"
                        sweetAlertDialog.contentTextSize = 8
                        *//*sweetAlertDialog.setConfirmClickListener {
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.bReplace, HomeFragment()).commit()
                            sweetAlertDialog.hide()
                        }*/
                        Toast.makeText(requireContext(), "You have completed Assessment and now go for rating phase", Toast.LENGTH_SHORT).show()

                        val resultIDBundle = Bundle().apply {
                            putString("result_id", result_id.toString())
                        }
                        val workAttitudeRatingPhaseFragment = WorkAttitudeRatingPhaseFragment()
                        workAttitudeRatingPhaseFragment.arguments = resultIDBundle

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.bReplace, workAttitudeRatingPhaseFragment)
                            .commit()

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

    private fun getAttitudeQuestions(page_no: Int) {

        workAttitudeAssessmentQuestionsFragmentViewModel.getAttitudeQuestions(page_no)

    }

    private fun clickNextBtn() {
        binding.nextBtn.setOnClickListener {

            if(prev_opened_page - page_no == 1 ){
                Log.d(TAG, attiquestPrefList.toString())
                Toast.makeText(requireContext(), "First Deselect All and then Reselect All", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prev_opened_page = -1
            if (attiquestPrefList.size <5){
                Toast.makeText(requireContext(), "Please Rank All the Options", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, attiquestPrefList.toString())

            saveAttitudeQuestionsRequest = SaveAttitudeQuestionsRequest(result_id.toString(), page_no.toString(), attiquestPrefList)

            workAttitudeAssessmentQuestionsFragmentViewModel.saveAttitudeQuestions(saveAttitudeQuestionsRequest)

            // if save successful then it will go to next page which is written in success part of saveAttitudeQuestions
        }

    }

    private fun clickPrevBtn() {
        binding.prevBtn.setOnClickListener {
            if (page_no > 1) {
                prev_opened_page = page_no
                getAttitudeQuestions(--page_no)
            } else Toast.makeText(
                requireContext(),
                "This is first page, can't back to previous page",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    private fun clickDeselectAll() {
        binding.deSelectAllBtn.setOnClickListener {
            for (item in attitudeAdapter.currentList) {
                // Log.d(TAG, attitudeAdapter.currentList.toList().toString())
                item.isSelected = false
            }
            attitudeAdapter.notifyItemRangeChanged(0, attitudeAdapter.itemCount)
            attiquestPrefList.clear()
            prev_opened_page = -1
        }
    }

    override fun onItemClicked(attiquestID: Int, attiquest: String) {
        Log.d(TAG, "$attiquestID")

        attiquestPrefList.add("$attiquestID")

    }

}