package com.example.margdarshakendra.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.margdarshakendra.utils.Constants.TAG
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {

    lateinit var smsBroadcastReceiverListener : SmsBroadcastReceiverListener

    override fun onReceive(context: Context?, intent: Intent?) {

        if( intent !=null && (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION)){
            val extras = intent.extras

            if(extras != null){
                val smsRetrieverStatus : Status? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(SmsRetriever.EXTRA_STATUS , Status::class.java)
                } else {
                    extras.get(SmsRetriever.EXTRA_STATUS) as Status
                }

                if(smsRetrieverStatus != null){
                    if(smsRetrieverStatus.statusCode == CommonStatusCodes.SUCCESS){
                        val messageIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT , Intent::class.java)
                        } else {
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                        }
                        if(messageIntent != null){
                            smsBroadcastReceiverListener.onOtpReceived(messageIntent)
                        }
                    }
                    else if(smsRetrieverStatus.statusCode == CommonStatusCodes.TIMEOUT){
                        smsBroadcastReceiverListener.onFailure()
                    }
                    else {
                        Log.d(TAG, smsRetrieverStatus.statusMessage.toString())
                    }
                }



            }



        }
    }

    interface SmsBroadcastReceiverListener {

        fun onOtpReceived(intent: Intent?)
        fun onFailure()
    }
}

