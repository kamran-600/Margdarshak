package com.margdarshakendra.margdarshak.broadcastReceivers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.margdarshakendra.margdarshak.LoginActivity
import com.margdarshakendra.margdarshak.R
import com.margdarshakendra.margdarshak.utils.Constants
import com.margdarshakendra.margdarshak.utils.Constants.TAG


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val i = Intent(context, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        if(intent == null || context == null) return
        val contentText = intent.getStringExtra("contentText")
        val title = intent.getStringExtra("title")
        val imageUrl = intent.getStringExtra("imageUrl")
        val notifyId = intent.getStringExtra("notifyId")
        Log.d(TAG, contentText.toString())
        Log.d(TAG, title.toString())
        Log.d(TAG, imageUrl.toString())
        Log.d(TAG, notifyId.toString())
        val pendingIntent = PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), i, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, notifyId!!)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(contentText)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setSound(getCustomSoundUri(context), AudioManager.STREAM_NOTIFICATION)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val description = "Channel for Alarm Manager"
            val channel = NotificationChannel(notifyId, name, NotificationManager.IMPORTANCE_HIGH)

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


    }


    private fun getCustomSoundUri(context: Context): Uri? {
        // Get the resource identifier of the custom sound
        val soundResourceId: Int = R.raw.notification_sound
        // Return the URI of the custom sound
        return Uri.parse("android.resource://" + context.packageName + "/" + soundResourceId)
    }


}
