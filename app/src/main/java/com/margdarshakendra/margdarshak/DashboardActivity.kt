package com.margdarshakendra.margdarshak

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.messaging.FirebaseMessaging
import com.margdarshakendra.margdarshak.adapters.DrawerExpandableMenuListAdapter
import com.margdarshakendra.margdarshak.assessment_fragments.PsychometricAptitudeAssessmentFragment
import com.margdarshakendra.margdarshak.assessment_fragments.SkillTestFragment
import com.margdarshakendra.margdarshak.assessment_fragments.WorkAttitudeAssessmentFragment
import com.margdarshakendra.margdarshak.broadcastReceivers.NotificationReceiver
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.CounsellingFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HiringFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.HomeFragment
import com.margdarshakendra.margdarshak.dashboard_bottom_fragments.WebEmailFragment
import com.margdarshakendra.margdarshak.databinding.ActivityDashboardBinding
import com.margdarshakendra.margdarshak.interview_fragments.CommunicationTestFragment
import com.margdarshakendra.margdarshak.interview_fragments.DocsUploadFragment
import com.margdarshakendra.margdarshak.interview_fragments.HrInterviewIntevieweeFragment
import com.margdarshakendra.margdarshak.interview_fragments.ScheduleReminderFragment
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

        if (sharedPreference.getDetail(
                Constants.USERTYPE,
                "String"
            ) == "S" && sharedPreference.getDetail(Constants.ASSOCIATE, "Int") == 0
        ) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.bReplace, StudentHomeFragment()).commit()

            binding.bottomNavigationView.visibility = View.GONE
        } else {

           // handleMenuDialog() old code

            if(intent != null && intent.getStringExtra("OpenFragment") != null){
                if(intent.getStringExtra("OpenFragment") == "counselling"){
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, CounsellingFragment()).commit()
                    binding.bottomNavigationView.selectedItemId = R.id.counselling
                }
                else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, HiringFragment()).commit()
                    binding.bottomNavigationView.selectedItemId = R.id.hiring
                }
            }else{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.bReplace, HomeFragment()).commit()
                binding.bottomNavigationView.selectedItemId = R.id.home
            }


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

                    /*else -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.bReplace, WhatsappFragment()).commit()
                    }*/
                }
                true
            }

            binding.bottomNavigationView.setOnItemReselectedListener {
                // empty body for preventing reselection
            }

        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, it.result)
                val token = it.result

                if (token != sharedPreference.getDetail(FCMTOKEN, "String")) {
                    dashboardViewModel.saveFcmTokenRequest(SaveFcmTokenRequest(token))
                } else {
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
                    sharedPreference.saveDetail(FCMTOKEN, it.data.token, "String")
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

        getUserAccessData()


        binding.toolBar.overflowIcon?.setTint(Color.BLACK)
        binding.toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    logout()
                }

                /*R.id.shop -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.bReplace, ProgressMeterFragment()).commit()
                }

                else -> {

                }*/
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
                    sharedPreference.saveDetail(Constants.ASSOCIATE, it.data.associate, "Int")

                    it.data.profile.meet_link?.let { it1 ->
                        sharedPreference.saveDetail(
                            Constants.USERMEETLINK,
                            it1, "String"
                        )
                    }

                    Glide.with(this).load(it.data.profile.pic).into(binding.profileImage)
                    binding.name.text = it.data.profile.name

                    val expandableListDetail = HashMap<String, MutableList<String>>()
                    val groupTitles = mutableListOf<String>()

                    for (i in it.data.links) {
                        if (expandableListDetail.containsKey(i.link_group)) {
                            val expandableGroupTitles = expandableListDetail[i.link_group]!!
                            expandableGroupTitles.add(i.link)
                            expandableListDetail[i.link_group] = expandableGroupTitles
                        } else {
                            groupTitles.add(i.link_group)
                            expandableListDetail[i.link_group] = mutableListOf(i.link)
                        }
                    }
                    Log.d(TAG, "api $expandableListDetail")

                    val expandableListAdapter =
                        DrawerExpandableMenuListAdapter(this, groupTitles, expandableListDetail)
                    binding.expandableListView.setAdapter(expandableListAdapter)

                    for (i in 0..<groupTitles.size) {
                        Log.d(TAG, expandableListAdapter.getGroup(i))
                        Log.d(TAG, expandableListAdapter.getChildrenCount(i).toString())
                        for (j in 0..<expandableListAdapter.getChildrenCount(i))
                            Log.d(TAG, expandableListAdapter.getChild(i, j))
                    }

                    binding.expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                        // Handle child item click
                        val clickedItemTitle =
                            expandableListDetail[expandableListAdapter.getGroup(groupPosition)]?.get(
                                childPosition
                            )
                        when (clickedItemTitle) {
                            "Aptitude Assessment (Free)" -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.bReplace,
                                        PsychometricAptitudeAssessmentFragment()
                                    ).commit()
                            }

                            "Attitude Assessment (Free)" -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.bReplace,
                                        WorkAttitudeAssessmentFragment()
                                    ).commit()
                            }

                            "Give Test" -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.bReplace, SkillTestFragment())
                                    .commit()
                            }

                            "Study Organiser" -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.bReplace, ProgressMeterFragment())
                                    .commit()
                            }

                            "Give Interview" -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.bReplace, HrInterviewIntevieweeFragment())
                                    .commit()
                            }

                            "communication test" -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.bReplace, CommunicationTestFragment())
                                    .commit()
                            }

                            "Document Upload" -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.bReplace, DocsUploadFragment())
                                    .commit()
                            }

                            "Schedule Reminder" -> {
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.bReplace, ScheduleReminderFragment())
                                    .commit()
                            }
                            "update profile" -> {
                                startActivity(Intent(this, ProfileActivity::class.java))
                            }


                        }
                        binding.drawerLayout.closeDrawer(GravityCompat.START, true)
                        true
                    }


                    /*val drawerMenu = binding.navigationView.menu

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

                    }*/


                    /**Schedule Notification*/

                    var totalNotifications = 0
                    for (notification in it.data.notifications) {
                        val calendar = getTime(notification.scheduled_time) ?: return@observe
                        if (calendar.timeInMillis < System.currentTimeMillis()) {
                            dashboardViewModel.deleteNotification(notification.notifyID)
                            continue
                        }
                        val interval = notification.reminder.toInt()
                        calendar.add(Calendar.MINUTE, interval)
                        for (j in 1..notification.reminder_count.toInt()) {
                            calendar.add(Calendar.MINUTE, (-1) * (interval))
                            if (calendar.timeInMillis < System.currentTimeMillis()) {
                                break
                            }
                            scheduleNotification(
                                calendar.timeInMillis,
                                notification.task_name,
                                notification.task,
                                null,
                                notification.notifyID.toString()
                            )
                            ++totalNotifications
                            Log.d(TAG, "notification scheduled at ${calendar.time}")
                        }
                        dashboardViewModel.deleteNotification(notification.notifyID)
                    }
                    if (totalNotifications > 0) {
                        Toast.makeText(
                            this,
                            "Total Scheduled Reminders : $totalNotifications",
                            Toast.LENGTH_SHORT
                        ).show()
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

        dashboardViewModel.deleteNotificationLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                }

                is NetworkResult.Error -> {
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                }
            }
        }


        val logoutActionView = binding.toolBar.menu.findItem(R.id.logout).actionView
        val progressBar = logoutActionView?.findViewById<ProgressBar>(R.id.progressBar)
        val logoutIcon = logoutActionView?.findViewById<ShapeableImageView>(R.id.logoutIcon)


        dashboardViewModel.logoutResponseLiveData.observe(this) {
            progressBar?.visibility = View.GONE
            when (it) {
                is NetworkResult.Success -> {
                    Log.d(TAG, it.data!!.toString())
                    sharedPreference.clearAll()
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()

                }

                is NetworkResult.Error -> {
                    binding.toolBar.menu.findItem(R.id.logout).isEnabled = true
                    if (it.message.equals("Unauthenticated.")) {
                        sharedPreference.clearAll()
                        Toast.makeText(this, "logged Out", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finishAffinity()
                    } else Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, it.message.toString())
                }

                is NetworkResult.Loading -> {
                    binding.toolBar.menu.findItem(R.id.logout).isEnabled = false
                    progressBar?.visibility = View.VISIBLE

                }
            }
        }

    }


    /*
        private fun handleMenuDialog(){

            val menuDialogBuilder = MaterialAlertDialogBuilder(this)
            val introMenuDialogLayoutBinding = IntroMenuDialogLayoutBinding.inflate(LayoutInflater.from(this))
            menuDialogBuilder.setView(introMenuDialogLayoutBinding.root)
            val menuDialog = menuDialogBuilder.create()
            menuDialog.setCanceledOnTouchOutside(false)
            menuDialog.show()

            introMenuDialogLayoutBinding.clientsData.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.bReplace, HomeFragment()).commit()
                binding.bottomNavigationView.selectedItemId = R.id.home
                menuDialog.hide()
            }

            introMenuDialogLayoutBinding.counsellingData.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.bReplace, CounsellingFragment()).commit()
                binding.bottomNavigationView.selectedItemId = R.id.counselling
                menuDialog.hide()
            }

            introMenuDialogLayoutBinding.hiringData.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.bReplace, HiringFragment()).commit()
                binding.bottomNavigationView.selectedItemId = R.id.hiring
                menuDialog.hide()
            }

            introMenuDialogLayoutBinding.webEmail.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.bReplace, WebEmailFragment()).commit()
                binding.bottomNavigationView.selectedItemId = R.id.webEmail
                menuDialog.hide()
            }

            menuDialog.setOnDismissListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.bReplace, HomeFragment()).commit()
                binding.bottomNavigationView.selectedItemId = R.id.home
            }
        }
    */

    private fun getTime(timeStamp: String): Calendar? {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            val date = format.parse(timeStamp)
            if (date != null) {
                val calender = Calendar.getInstance()
                //Log.d(TAG, date.toString())
                calender.time = date
                //calender.add(Calendar.MINUTE, -10)
                //Log.d(TAG, calender.time.toString())
                return calender
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
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


    private fun scheduleNotification(
        timeInMillis: Long,
        title: String,
        message: String,
        imageUrl: String? = null,
        notificationID: String
    ) {

        if (Calendar.getInstance().timeInMillis > timeInMillis) {
            return
        }

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(applicationContext, NotificationReceiver::class.java)
        notificationIntent.action =
            "com.margdarshakendra.margdarshak.ACTION_SHOW_NOTIFICATION" // Use the same action string
        notificationIntent.putExtra("contentText", message)
        notificationIntent.putExtra("title", title)
        notificationIntent.putExtra("imageUrl", imageUrl)
        notificationIntent.putExtra("notifyId", notificationID)
        //notificationIntent.putExtra("requestCode", requestCode)

        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, requestCode, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )



        Log.d(TAG, message)
        Log.d(TAG, title)
        Log.d(TAG, imageUrl.toString())
        Log.d(TAG, notificationID)
        Log.d(TAG, requestCode.toString())

        try {
            /*if (Calendar.getInstance().timeInMillis > timeInMillis) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, pendingIntent)
            }
            else*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }


        } catch (e: SecurityException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

}
