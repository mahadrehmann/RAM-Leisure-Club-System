package com.arshman.mahad.rehan

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.onesignal.OneSignal

/**
 * Service that handles FCM tokens and messages.
 * Note: OneSignal typically handles most FCM integration automatically,
 * but this service allows for custom behavior if needed.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCMService"

    /**
     * Called when a new FCM token is generated
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token received: $token")

        // OneSignal automatically syncs FCM tokens when the app starts
        // but we can manually trigger a sync if needed
        try {
            // Note: In OneSignal SDK 5.0+, you don't need to manually sync the FCM token
            // as it's handled automatically through the OneSignal initialization process
            Log.d(TAG, "OneSignal will automatically sync this FCM token")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing FCM token with OneSignal", e)
        }
    }

    /**
     * Called when a FCM message is received
     * Note: OneSignal will normally handle most FCM messages directly
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "FCM message received from: ${remoteMessage.from}")

        // Check if message contains data
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "FCM Message data payload: ${remoteMessage.data}")

            // Check if this is a OneSignal push
            if (remoteMessage.data.containsKey("custom")) {
                Log.d(TAG, "This appears to be a OneSignal message - it will be handled by OneSignal SDK")
                super.onMessageReceived(remoteMessage)
                return
            }

            // Handle your custom FCM messages here if needed
            val notificationType = remoteMessage.data["notificationType"]
            if (notificationType == "booking_confirmation") {
                val bookingId = remoteMessage.data["bookingId"]
                Log.d(TAG, "Custom booking confirmation for booking ID: $bookingId")
                // Handle custom data processing here
            }
        }

        // Check if message contains notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "FCM Message notification body: ${it.body}")
            // Handle notification if needed
        }

        // Let the parent implementation handle other aspects
        super.onMessageReceived(remoteMessage)
    }
}