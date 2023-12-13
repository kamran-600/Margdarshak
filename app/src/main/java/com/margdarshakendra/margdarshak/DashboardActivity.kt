package com.margdarshakendra.margdarshak

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.SubMenu
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.margdarshakendra.margdarshak.assessment_fragments.PsychometricAptitudeAssessmentFragment
import com.margdarshakendra.margdarshak.assessment_fragments.WorkAttitudeAssessmentFragment
import com.margdarshakendra.margdarshak.broadcastReceivers.NotificationReceiver
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.CounsellingFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HiringFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.WebEmailFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.WhatsappFragment
import com.margdarshakendra.margdarshak.databinding.ActivityDashboardBinding
import com.margdarshakendra.margdarshak.databinding.DashboardHeaderBinding
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NetworkResult
import com.margdarshakendra.margdarshak.utils.SharedPreference
import com.margdarshakendra.margdarshak.viewmodels.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.HashSet
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {


    private var _binding: ActivityDashboardBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel by viewModels<DashboardViewModel>()

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent : PendingIntent


    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val headerBinding = DashboardHeaderBinding.inflate(LayoutInflater.from(this))
        binding.navigationView.addHeaderView(headerBinding.root)

        getUserAccessData()



        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolBar, R.string.open_drawer, R.string.close_drawer
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


        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
               /* R.id.aptitude_assessment_free -> supportFragmentManager.beginTransaction()
                    .replace(R.id.bReplace, PsychometricAptitudeAssessmentFragment()).commit()

               R.id.attitude_assessment_free -> supportFragmentManager.beginTransaction()
                    .replace(R.id.bReplace, WorkAttitudeAssessmentFragment()).commit()

                else -> {

                    *//*if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            postNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        else scheduleNotification(Calendar.getInstance().timeInMillis+5000)
                    } else {
                        scheduleNotification(Calendar.getInstance().timeInMillis+5000)
                    }*//*
                }*/
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        if (sharedPreference.getDetail(Constants.USERTYPE, "String") == "S"){
            binding.studentLoginData.visibility = View.VISIBLE
            binding.bottomNavigationView.visibility = View.GONE
        }
        else {

            supportFragmentManager.beginTransaction()
                .replace(R.id.bReplace, HomeFragment()).commit()

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



                    for(i in it.data.links){
                       // if(i.mobileapp_id != null ){
                            // Add a new menu item dynamically
                            val newItemTitle = i.link

                        if(! map.containsKey(i.link_group)){
                                map[i.link_group] = submenuI
                                drawerMenu.add(Menu.NONE, submenuI, Menu.NONE, i.link_group).isEnabled = false
                                Log.d(TAG, "${i.link_group} is added")
                                // val drawerSubmenu = drawerMenu.addSubMenu(Menu.NONE, submenuI++, Menu.NONE, i.link_group)

                                menuMap[newItemTitle] = ++k
                                drawerMenu.add(submenuI, k, Menu.NONE, newItemTitle)
                                    .setOnMenuItemClickListener {item ->
                                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                                        if(item.itemId == menuMap[newItemTitle]){
                                            if(newItemTitle == "Aptitude Assessment (Free)") {
                                                supportFragmentManager.beginTransaction()
                                                    .replace(R.id.bReplace, PsychometricAptitudeAssessmentFragment()).commit()
                                            }
                                            else if(newItemTitle == "Attitude Assessment (Free)"){
                                                supportFragmentManager.beginTransaction()
                                                    .replace(R.id.bReplace, WorkAttitudeAssessmentFragment()).commit()
                                            }
                                        }
                                        true
                                    }
                                Log.d(TAG, "${i.link} is added")
                                submenuI++
                            }
                            else{
                                menuMap[newItemTitle] = ++k
                                drawerMenu.add(map[i.link_group]!!, k, Menu.NONE, newItemTitle)
                                    .setOnMenuItemClickListener {item ->
                                        binding.drawerLayout.closeDrawer(GravityCompat.START,true)
                                        if(item.itemId == menuMap[newItemTitle]){
                                            if(newItemTitle == "Aptitude Assessment (Free)") {
                                                supportFragmentManager.beginTransaction()
                                                    .replace(R.id.bReplace, PsychometricAptitudeAssessmentFragment()).commit()
                                            }
                                            else if(newItemTitle == "Attitude Assessment (Free)"){
                                                supportFragmentManager.beginTransaction()
                                                    .replace(R.id.bReplace, WorkAttitudeAssessmentFragment()).commit()
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

    }

    private fun getUserAccessData() {
        dashboardViewModel.getUserAccess()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /*private fun scheduleNotification(timeInMillies : Long){

        createNotificationChannel()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, NotificationReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillies , pendingIntent  )
            Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show()

        } catch ( e : SecurityException){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }*/

    /*private fun createNotificationChannel(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val description = "Channel for Alarm Mananger"
            val channel = NotificationChannel(Constants.CHANNELID, name, NotificationManager.IMPORTANCE_HIGH)

            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)

        }
    }
    
    val postNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
            scheduleNotification(Calendar.getInstance().timeInMillis + 50000)
        }
        else {
            Toast.makeText(this, "Please allow notification permission to receive reminder", Toast.LENGTH_SHORT).show()
        }
    }*/

    fun <K, V> LinkedHashMap<K, V>.indexOfKey(key: K): Int {
        val iterator = this.entries.iterator()
        var index = 0

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == key) {
                return index
            }
            index++
        }

        // Key not found
        return -1
    }

}