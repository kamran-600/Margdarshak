package com.margdarshakendra.margdarshak.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.CalenderFragment
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.ShowProgressMeterFragment
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.CompareFragment
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.EfficiencyFragment
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.InteractiveClassFragment
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.PerformanceFragment
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.ResultsFragment
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.StudyOrganiserFragment

class TabPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    val tabTitles = arrayOf(
        "Study-organiser",
        "Interactive Class",
        "Results",
        "Efficiency",
        "Performance",
        "Compare",
        "Progress Meter",
        "Calender"
    )

    override fun getItemCount(): Int = tabTitles.size

    override fun createFragment(position: Int): Fragment {
        // Return the fragment for the corresponding tab position
        return when (position) {
            0 -> StudyOrganiserFragment()
            1 -> InteractiveClassFragment()
            2 -> ResultsFragment()
            3 -> EfficiencyFragment()
            4 -> PerformanceFragment()
            5 -> CompareFragment()
            6 -> ShowProgressMeterFragment()
            7 -> CalenderFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
