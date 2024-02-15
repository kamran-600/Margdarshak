package com.margdarshakendra.margdarshak.dashboard_bottom_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.faltenreich.skeletonlayout.SkeletonConfig
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.adapters.ClientDataRecAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentHomeBinding
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.HomeViewModel
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.margdarshakendra.margdarshak.databinding.ShortlistForHiringDialogLayoutBinding
import com.margdarshakendra.margdarshak.models.GetForCounsellingRequest
import com.margdarshakendra.margdarshak.models.ShortListUserRequest
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    private val homeViewModel by viewModels<HomeViewModel>()

    private var employerId = 0
    private var employerPostId = 0
    private lateinit var employerMap: LinkedHashMap<String, Int>

    private lateinit var clientDataRecAdapter : ClientDataRecAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container,false)

        employerMap = LinkedHashMap()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clientDataRecAdapter = ClientDataRecAdapter(requireContext(), ::showPopMenu)
        binding.clientDataRecyclerView.setHasFixedSize(true)
        binding.clientDataRecyclerView.adapter = clientDataRecAdapter

        getClientData()

        binding.swipeRefresh.setOnRefreshListener {
            getClientData()
        }

        val skeleton = binding.clientDataRecyclerView.applySkeleton(R.layout.single_row_client_data, 5,
            SkeletonConfig(
                maskColor = ContextCompat.getColor(requireContext(), SkeletonLayout.DEFAULT_MASK_COLOR),
                maskCornerRadius = 150f,
                showShimmer = SkeletonLayout.DEFAULT_SHIMMER_SHOW,
                shimmerColor = ContextCompat.getColor(requireContext(), SkeletonLayout.DEFAULT_SHIMMER_COLOR),
                shimmerDurationInMillis = SkeletonLayout.DEFAULT_SHIMMER_DURATION_IN_MILLIS,
                shimmerDirection = SkeletonLayout.DEFAULT_SHIMMER_DIRECTION,
                shimmerAngle = SkeletonLayout.DEFAULT_SHIMMER_ANGLE))

        homeViewModel.clientDataResponseLiveData.observe(viewLifecycleOwner) {
            skeleton.showOriginal()
            when (it) {
                is NetworkResult.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    Log.d(TAG, it.data!!.toString())

                    clientDataRecAdapter.submitList(null)
                    clientDataRecAdapter.submitList(it.data.data)


                    if (!it.data.employers.isNullOrEmpty()) {
                        for (i in it.data.employers) {
                            if (i != null) employerMap[i.name] = i.userID
                        }
                    }

                }

                is NetworkResult.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    skeleton.showSkeleton()
                }
            }
        }


    }

    private fun showPopMenu(view: View, uid: Int, userName: String, userType : String) {
        Log.d(TAG, "showPopMenu is clicked")
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.clients_pop_menu, popupMenu.menu)

        popupMenu.menu.findItem(R.id.getForCounselling).isVisible = userType == "S" // If student then visible

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.shortListForHiring -> {

                    employerId = 0
                    employerPostId = 0
                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())

                    val shortlistForHiringDialogLayoutBinding =
                        ShortlistForHiringDialogLayoutBinding.inflate(
                            LayoutInflater.from(
                                requireContext()
                            )
                        )

                    dialogBuilder.setView(shortlistForHiringDialogLayoutBinding.root)
                    val materialAlertDialog = dialogBuilder.create()
                    materialAlertDialog.show()

                    materialAlertDialog.setCanceledOnTouchOutside(false)

                    shortlistForHiringDialogLayoutBinding.userName.text = userName
                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setAdapter(
                        null
                    )
                    shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setAdapter(
                        null
                    )

                    val employerListAdapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item,
                        employerMap.keys.toList()
                    )
                    employerListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setAdapter(
                        employerListAdapter
                    )

                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                        employerId =
                            employerMap[shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.text.toString()]!!
                        shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setAdapter(null)
                        shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.text = null
                        employerPostId = 0
                        homeViewModel.getEmployerPostsData(employerId)
                        Log.d(TAG, employerId.toString())
                    }
                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setOnClickListener {
                        shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.showDropDown()
                    }
                    shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            shortlistForHiringDialogLayoutBinding.employerAutoCompleteTextView.showDropDown()
                        }
                    }

                    homeViewModel.employerPostsDataLiveData.observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is NetworkResult.Success -> {
                                Log.d(TAG, response.data!!.toString())

                                val employerPostMap = LinkedHashMap<String, Int>()

                                if (!response.data.data.isNullOrEmpty()) {
                                    for (i in response.data.data) {
                                        employerPostMap[i.position] = i.postID
                                    }
                                } else return@observe

                                val employerPostListAdapter = ArrayAdapter(
                                    requireContext(), android.R.layout.simple_spinner_dropdown_item,
                                    employerPostMap.keys.toList()
                                )
                                employerPostListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setAdapter(
                                    employerPostListAdapter
                                )

                                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
                                    employerPostId =
                                        employerPostMap[shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.text.toString()]!!
                                    Log.d(TAG, employerPostId.toString())

                                }
                                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setOnClickListener {
                                    shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.showDropDown()
                                }
                                shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
                                    if (hasFocus) {
                                        shortlistForHiringDialogLayoutBinding.empPostAutoCompleteTextView.showDropDown()
                                    }
                                }
                            }

                            is NetworkResult.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    response.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d(TAG, response.message.toString())
                            }

                            is NetworkResult.Loading -> {

                            }
                        }
                    }

                    shortlistForHiringDialogLayoutBinding.cancelButton.setOnClickListener {
                        materialAlertDialog.dismiss()
                    }
                    shortlistForHiringDialogLayoutBinding.close.setOnClickListener {
                        materialAlertDialog.dismiss()
                    }
                    shortlistForHiringDialogLayoutBinding.submit.setOnClickListener {
                        if (employerId == 0) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            sweetAlertDialog.contentText = "Please Select Employer"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        if (employerPostId == 0) {
                            val sweetAlertDialog =
                                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            sweetAlertDialog.contentText = "Please Select Employer Position"
                            sweetAlertDialog.confirmText = "OK"
                            sweetAlertDialog.show()
                            return@setOnClickListener
                        }
                        val shortListUserRequest = ShortListUserRequest(
                            uid.toString(),
                            employerId.toString(),
                            employerPostId.toString()
                        )
                        homeViewModel.shortListUser(shortListUserRequest)
                        Log.d(TAG, shortListUserRequest.toString())

                        val sweetLoadingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
                        sweetLoadingDialog.contentText = "Please Wait..."
                        sweetLoadingDialog.setCanceledOnTouchOutside(false)
                        homeViewModel.shortListUserResponseLiveData.observe(viewLifecycleOwner) { response ->
                            sweetLoadingDialog.dismiss()
                            when (response) {
                                is NetworkResult.Success -> {
                                    Log.d(TAG, response.data!!.toString())
                                    val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    sweetAlertDialog.contentText = response.data.message
                                    sweetAlertDialog.confirmText = "OK"
                                    sweetAlertDialog.show()
                                    Toast.makeText(requireContext(), response.data.message, Toast.LENGTH_SHORT).show()
                                    materialAlertDialog.dismiss()
                                    homeViewModel.shortListUserResponseLiveData.removeObservers(viewLifecycleOwner)
                                    homeViewModel.employerPostsDataLiveData.removeObservers(viewLifecycleOwner)

                                }

                                is NetworkResult.Error -> {
                                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, response.message.toString())
                                    materialAlertDialog.dismiss()
                                    homeViewModel.shortListUserResponseLiveData.removeObservers(viewLifecycleOwner)
                                    homeViewModel.employerPostsDataLiveData.removeObservers(viewLifecycleOwner)
                                }

                                is NetworkResult.Loading -> {
                                        sweetLoadingDialog.show()
                                }
                            }
                        }

                        materialAlertDialog.setOnDismissListener {
                            homeViewModel.employerPostsDataLiveData.removeObservers(viewLifecycleOwner)
                            homeViewModel.shortListUserResponseLiveData.removeObservers(viewLifecycleOwner)
                        }
                    }

                }

                R.id.getForCounselling -> {
                    getForCounselling(uid)

                    val sweetLoadingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)
                    sweetLoadingDialog.contentText = "Please Wait"
                    sweetLoadingDialog.setCanceledOnTouchOutside(false)
                    homeViewModel.getForCounsellingResponseLiveData.observe(viewLifecycleOwner){response ->
                        sweetLoadingDialog.dismiss()
                        when (response) {
                            is NetworkResult.Success -> {
                                val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                Log.d(TAG, response.data!!.toString())
                                sweetAlertDialog.contentText = response.data.message
                                sweetAlertDialog.confirmText = "OK"
                                sweetAlertDialog.show()
                                Toast.makeText(requireContext(), response.data.message, Toast.LENGTH_SHORT).show()
                                homeViewModel.getForCounsellingResponseLiveData.removeObservers(viewLifecycleOwner)
                            }

                            is NetworkResult.Error -> {
                                val sweetAlertDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                sweetAlertDialog.contentText = response.message
                                sweetAlertDialog.confirmText = "OK"
                                sweetAlertDialog.show()
                                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                                Log.d(TAG, response.message.toString())

                                homeViewModel.getForCounsellingResponseLiveData.removeObservers(viewLifecycleOwner)

                            }

                            is NetworkResult.Loading -> {
                                sweetLoadingDialog.show()
                            }
                        }
                    }
                }
            }
            true
        }
        popupMenu.show()

    }

    private fun getForCounselling(uid: Int){
        val getForCounsellingRequest = GetForCounsellingRequest(uid)
        Log.d(TAG, getForCounsellingRequest.toString())
        homeViewModel.getForCounselling(getForCounsellingRequest)
    }

    private fun getClientData() {
      //  val dataRequest = DataRequest("clients")
        homeViewModel.getClientData("clients")
    }

}