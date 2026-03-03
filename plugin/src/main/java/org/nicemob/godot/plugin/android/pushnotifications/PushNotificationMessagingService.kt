package org.nicemob.godot.plugin.android.pushnotifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class PushNotificationMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "GodotPushNotifications"
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "FCM token refreshed: $token")
    }
}
