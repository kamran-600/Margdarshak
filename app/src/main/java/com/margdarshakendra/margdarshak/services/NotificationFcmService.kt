package com.margdarshakendra.margdarshak.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony.Sms.Intents
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.margdarshakendra.margdarshak.LoginActivity
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.broadcastReceivers.NotificationReceiver
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import com.margdarshakendra.margdarshak.utils.NotificationUtils
import com.margdarshakendra.margdarshak.utils.SharedPreference
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class NotificationFcmService : FirebaseMessagingService() {


    // private val TAG = "MessagingService"

    private var notificationUtils: NotificationUtils? = null


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, remoteMessage.sentTime.toString())

        Log.d(TAG, "From: " + remoteMessage.from)
        Log.d(TAG, remoteMessage.data.toString())

        Log.d(TAG, remoteMessage.notification?.title + remoteMessage.notification?.body)

        val title = remoteMessage.notification?.title!!
        val message = remoteMessage.notification?.body!!

        Log.d(TAG, remoteMessage.data["action"]+remoteMessage.data["url"]+remoteMessage.data["time"])

        handleDataMessage(remoteMessage.data, title, message)

    }

    private fun handleDataMessage(data : Map<String, String>, title: String, message: String) {
        try {

            val action = data["action"]
            val imageUrl = data["url"]
            val timestamp = data["time"]!!

           scheduleNotification(getTimeMilliSecBefore10Minutes(timestamp), title, message, imageUrl)

            /*val resultIntent = Intent(applicationContext, LoginActivity::class.java)
            // check for image attachment
            if (imageUrl.isNullOrEmpty()) {
                showNotificationMessage(applicationContext, title, message, timestamp, resultIntent)
            } else {
                // image is present, show notification with image
                showNotificationMessageWithBigImage(applicationContext, title, message, timestamp, resultIntent,imageUrl)
            }*/

        } catch (e: Exception) {
            Log.d(TAG, "Exception: " + e.message)
        }

    }

    private fun scheduleNotification(timeInMillis: Long,title: String, message: String, imageUrl: String?) {



        createNotificationChannel()
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(applicationContext, NotificationReceiver::class.java)
        notificationIntent.action = "com.margdarshakendra.margdarshak.ACTION_SHOW_NOTIFICATION" // Use the same action string

        notificationIntent.putExtra("contentText", message)
        notificationIntent.putExtra("title", title)
        notificationIntent.putExtra("imageUrl", imageUrl)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Calendar.getInstance().timeInMillis > timeInMillis) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, pendingIntent)
            }
            else alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        } catch (e: SecurityException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }



    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val description = "Channel for Alarm Manager"
            val channel =
                NotificationChannel(Constants.CHANNELID, name, NotificationManager.IMPORTANCE_HIGH)

            channel.description = description
            val notificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)

        }

    }

 /**
     * Showing notification with text only
     */
    /*private fun showNotificationMessage(
        context: Context,
        title: String,
        message: String,
        timeStamp: String,
        intent: Intent
    ) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils?.showNotificationMessage(title, message, timeStamp, intent)
    }*/
    /**
     * Showing notification with text and image
     */
    /*private fun showNotificationMessageWithBigImage(
        context: Context,
        title: String,
        message: String,
        timeStamp: String,
        intent: Intent,
        imageUrl: String
    ) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils?.showNotificationMessage(title, message, timeStamp, intent, imageUrl)
    }*/


    private fun getTimeMilliSecBefore10Minutes(timeStamp: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        try {
            val date = format.parse(timeStamp)
            if (date != null) {
                val calender = Calendar.getInstance()
                Log.d(TAG, date.toString())
                calender.time = date
                calender.add(Calendar.MINUTE, -10)
                Log.d(TAG, calender.time.toString())
                return calender.timeInMillis
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }
}
