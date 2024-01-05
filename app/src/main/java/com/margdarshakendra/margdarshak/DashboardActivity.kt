package com.margdarshakendra.margdarshak

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.margdarshakendra.margdarshak.assessment_fragments.PsychometricAptitudeAssessmentFragment
import com.margdarshakendra.margdarshak.assessment_fragments.SkillTestFragment
import com.margdarshakendra.margdarshak.assessment_fragments.WorkAttitudeAssessmentFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.CounsellingFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HiringFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.WebEmailFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.WhatsappFragment
import com.margdarshakendra.margdarshak.databinding.ActivityDashboardBinding
import com.margdarshakendra.margdarshak.databinding.DashboardHeaderBinding
import com.margdarshakendra.margdarshak.models.LogoutRequest
import com.margdarshakendra.margdarshak.models.SaveFcmTokenRequest
import com.margdarshakendra.margdarshak.progress_meter_tab_fragments.ProgressMeterFragment
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.FCMTOKEN
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {


    private var _binding: ActivityDashboardBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel by viewModels<DashboardViewModel>()


    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, it.result)
                val token = it.result

                if(token != sharedPreference.getDetail(FCMTOKEN,"String")) {
                    dashboardViewModel.saveFcmTokenRequest(SaveFcmTokenRequest(token))
                }
                else{
                    Log.d(TAG, "token already exists")
                }
            } else {
                Log.d(TAG, it.exception?.message.toString())
            }
        }

        dashboardViewModel.saveFcmTokenLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    sharedPreference.saveDetail(FCMTOKEN,it.data.token, "String" )
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }

                else -> {}
            }
        }



        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                postNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val headerBinding = DashboardHeaderBinding.inflate(LayoutInflater.from(this))
        binding.navigationView.addHeaderView(headerBinding.root)

        getUserAccessData()

        binding.toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    logout()
                }

                R.id.shop -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, ProgressMeterFragment()).commit()
                }

                else -> {}
            }
            true
        }

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolBar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.drawer_icon)

        toggle.setToolbarNavigationClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S") {

            supportFragmentManager.beginTransaction()
                .replace(R.id.bReplace, StudentHomeFragment()).commit()

            binding.bottomNavigationView.visibility = View.GONE
        } else {

            supportFragmentManager.beginTransaction()
                .replace(R.id.bReplace, HomeFragment()).commit()

            binding.bottomNavigationView.selectedItemId = R.id.home

            binding.bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {

                    R.id.home -> supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, HomeFragment()).commit()

                    R.id.counselling -> supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, CounsellingFragment()).commit()

                    R.id.hiring ->
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.bReplace, HiringFragment()).commit()

                    R.id.webEmail -> supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, WebEmailFragment()).commit()

                    else -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.bReplace, WhatsappFragment()).commit()
                    }
                }
                true
            }

            binding.bottomNavigationView.setOnItemReselectedListener {
                // empty for preventing reselection
            }

        }



        dashboardViewModel.userAccessResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    sharedPreference.saveDetail(
                        Constants.USEREMAIL, it.data.profile.email, "String"
                    )
                    sharedPreference.saveDetail(
                        Constants.USERMOBILE, it.data.profile.mobile, "String"
                    )
                    sharedPreference.saveDetail(
                        Constants.USERTYPE, it.data.profile.usertype, "String"
                    )
                    sharedPreference.saveDetail(Constants.USERNAME, it.data.profile.name, "String")

                    Glide.with(this).load(it.data.profile.pic).into(headerBinding.profileImage)
                    headerBinding.name.text = it.data.profile.name

                    val drawerMenu = binding.navigationView.menu

                    var k = 0
                    var submenuI = 0
                    val map = HashMap<String, Int>()

                    val menuMap = HashMap<String, Int>()

                    map.clear()
                    menuMap.clear()

                    for (i in it.data.links) {
                        // if(i.mobileapp_id != null ){
                        // Add a new menu item dynamically
                        val newItemTitle = i.link

                        if (!map.containsKey(i.link_group)) {
                            map[i.link_group] = submenuI
                            drawerMenu.add(Menu.NONE, submenuI, Menu.NONE, i.link_group).isEnabled =
                                false
                            Log.d(TAG, "${i.link_group} is added")
                            // val drawerSubmenu = drawerMenu.addSubMenu(Menu.NONE, submenuI++, Menu.NONE, i.link_group)

                            menuMap[newItemTitle] = ++k
                            drawerMenu.add(submenuI, k, Menu.NONE, newItemTitle)
                                .setOnMenuItemClickListener { item ->
                                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                                    if (item.itemId == menuMap[newItemTitle]) {
                                        if (newItemTitle == "Aptitude Assessment (Free)") {
                                            supportFragmentManager.beginTransaction()
                                                .replace(
                                                    R.id.bReplace,
                                                    PsychometricAptitudeAssessmentFragment()
                                                ).commit()
                                        } else if (newItemTitle == "Attitude Assessment (Free)") {
                                            supportFragmentManager.beginTransaction()
                                                .replace(
                                                    R.id.bReplace,
                                                    WorkAttitudeAssessmentFragment()
                                                ).commit()
                                        } else if (newItemTitle == "Give Test") {
                                            supportFragmentManager.beginTransaction()
                                                .replace(R.id.bReplace, SkillTestFragment())
                                                .commit()
                                        } else if (newItemTitle == "Study Organiser") {
                                            supportFragmentManager.beginTransaction()
                                                .replace(R.id.bReplace, ProgressMeterFragment())
                                                .commit()
                                        }
                                    }
                                    true
                                }
                            Log.d(TAG, "${i.link} is added")
                            submenuI++
                        } else {
                            menuMap[newItemTitle] = ++k
                            drawerMenu.add(map[i.link_group]!!, k, Menu.NONE, newItemTitle)
                                .setOnMenuItemClickListener { item ->
                                    binding.drawerLayout.closeDrawer(GravityCompat.START, true)
                                    if (item.itemId == menuMap[newItemTitle]) {
                                        if (newItemTitle == "Aptitude Assessment (Free)") {
                                            supportFragmentManager.beginTransaction()
                                                .replace(
                                                    R.id.bReplace,
                                                    PsychometricAptitudeAssessmentFragment()
                                                ).commit()
                                        } else if (newItemTitle == "Attitude Assessment (Free)") {
                                            supportFragmentManager.beginTransaction()
                                                .replace(
                                                    R.id.bReplace,
                                                    WorkAttitudeAssessmentFragment()
                                                ).commit()
                                        } else if (newItemTitle == "Give Test") {
                                            supportFragmentManager.beginTransaction()
                                                .replace(R.id.bReplace, SkillTestFragment())
                                                .commit()
                                        } else if (newItemTitle == "Study Organiser") {
                                            supportFragmentManager.beginTransaction()
                                                .replace(R.id.bReplace, ProgressMeterFragment())
                                                .commit()
                                        }

                                    }
                                    true
                                }
                            Log.d(TAG, "${i.link}, ${map[i.link_group]!!} is added")
                        }
                        //   }

                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }

                else -> {}
            }
        }


        dashboardViewModel.logoutResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    sharedPreference.clearAll()
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()

                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {

                }

                else -> {}
            }
        }

    }

    private val postNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                Toast.makeText(
                    this,
                    "Please allow notification permission in app info/ Setting to receive reminder",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    private fun getUserAccessData() {
        dashboardViewModel.getUserAccess()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun logout() {
        val login_id = sharedPreference.getDetail(Constants.USERLOGINID, "Int") as Int
        val logoutRequest = LogoutRequest(login_id)
        dashboardViewModel.logout(logoutRequest)
    }
}