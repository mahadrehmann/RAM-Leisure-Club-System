package com.arshman.mahad.rehan

import android.app.Application
import android.util.Log
import com.onesignal.OneSignal

class ApplicationClass : Application() {
    companion object {
        const val ONESIGNAL_APP_ID = "e31abef6-4c88-4338-8d3e-d4fb59f34de1"
        private const val TAG = "ApplicationClass"
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize OneSignal
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        
        Log.d(TAG, "OneSignal initialized successfully")
    }
}