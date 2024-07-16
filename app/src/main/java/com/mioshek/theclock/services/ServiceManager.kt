package com.mioshek.theclock.services

import android.app.ActivityManager
import android.content.Context

class ServiceManager {
    companion object{
        fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            for (service in manager?.getRunningServices(Integer.MAX_VALUE) ?: emptyList()) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

         fun getRunningServicesCount(context: Context): Int {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

            val packageName = context.packageName
            val myAppServices = runningServices.filter { it.service.packageName == packageName }

            return myAppServices.size
        }

    }
}