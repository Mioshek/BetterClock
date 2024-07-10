package com.mioshek.theclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mioshek.theclock.services.RingtoneService


class ClockReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.stopService(Intent(context, RingtoneService::class.java))
    }
}
