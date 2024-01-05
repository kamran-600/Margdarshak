package com.margdarshakendra.margdarshak.progress_meter_tab_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.margdarshakendra.margdarshak.adapters.TabPagerAdapter
import com.margdarshakendra.margdarshak.databinding.FragmentProgressMeterBinding

class ProgressMeterFragment : Fragment() {

    private lateinit var binding: FragmentProgressMeterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProgressMeterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TabPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true
        ) { tab, position -> tab.text = adapter.tabTitles[position] }.attach()
    }

}