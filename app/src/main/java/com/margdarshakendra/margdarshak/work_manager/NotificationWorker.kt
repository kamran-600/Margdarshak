package com.margdarshakendra.margdarshak.work_manager

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.margdarshakendra.margdarshak.LoginActivity
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.broadcastReceivers.NotificationReceiver
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG
import java.util.Calendar

class NotificationWorker(private val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d(TAG, "worker is called !" )
        val bundleData = inputData.keyValueMap
        Log.d(TAG, bundleData.toString())
        val timestamp = bundleData["timestamp"] as Long
        val title = bundleData["title"] as String
        val message = bundleData["message"] as String
        val imageUrl = bundleData["imageUrl"] as String

        val i = Intent(context, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE)


        val notification = NotificationCompat.Builder(context, Constants.CHANNELID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val description = "Channel for Alarm Manager"
            val channel =
                NotificationChannel(Constants.CHANNELID, name, NotificationManager.IMPORTANCE_HIGH)

            channel.description = description

            notificationManager.createNotificationChannel(channel)

        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(Constants.NOTIFICATIONID, notification)
        }else {
            Toast.makeText(context, "Please allow notification permission in app info/ Setting to receive reminder", Toast.LENGTH_LONG).show()
        }



        return Result.success()
    }

}

private fun scheduleNotification(context: Context, timeInMillis: Long,title: String, message: String, imageUrl: String?) {

    createNotificationChannel(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val notificationIntent = Intent(context, NotificationReceiver::class.java)
    notificationIntent.action = "com.margdarshakendra.margdarshak.ACTION_SHOW_NOTIFICATION" // Use the same action string

    notificationIntent.putExtra("contentText", message)
    notificationIntent.putExtra("title", title)
    notificationIntent.putExtra("imageUrl", imageUrl)

    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, notificationIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    try {
        if (Calendar.getInstance().timeInMillis > timeInMillis) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis, pendingIntent)
        }
        else alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

    } catch (e: SecurityException) {
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
}

private fun createNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.app_name)
        val description = "Channel for Alarm Manager"
        val channel =
            NotificationChannel(Constants.CHANNELID, name, NotificationManager.IMPORTANCE_HIGH)

        channel.description = description
        val notificationManager =
            context.getSystemService(NotificationManager::class.java)

        notificationManager.createNotificationChannel(channel)

    }

}