package com.margdarshakendra.margdarshak.dashboard_bottom_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.adapters.ClientDataRecAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentHomeBinding
import com.margdarshakendra.margdarshak.models.DataRequest
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.viewmodels.HomeViewModel
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container,false)

        getClientData()

        val skeleton: Skeleton =
            binding.clientDataRecyclerView.applySkeleton(R.layout.single_row_clientdata, 5)
        skeleton.showSkeleton()
        homeViewModel.clientDataResponseLiveData.observe(requireActivity()) {
            when (it) {
                is NetworkResult.Success -> {
                    skeleton.showOriginal()
                    Log.d(Constants.TAG, it.data!!.toString())

                    val clientDataRecAdapter = ClientDataRecAdapter(requireContext())
                    clientDataRecAdapter.submitList(it.data.data)

                    binding.clientDataRecyclerView.setHasFixedSize(true)
                    binding.clientDataRecyclerView.adapter = clientDataRecAdapter

                }

                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }
            }
        }


        return binding.root
    }

    private fun showPopMenu(){
    }

    private fun getClientData() {
        val dataRequest = DataRequest("clients")
        homeViewModel.getClientData(dataRequest)
    }

}