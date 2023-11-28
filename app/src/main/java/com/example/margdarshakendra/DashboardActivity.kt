package com.example.margdarshakendra

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.margdarshakendra.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private var _binding: ActivityDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolBar, R.string.open_drawer, R.string.close_drawer)

        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.drawer_icon)

        toggle.setToolbarNavigationClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            else binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }





    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}