package org.nicemob.godot.plugin.android.pushnotifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class GodotAndroidPushNotificationsPlugin(godot: Godot) : GodotPlugin(godot) {

    companion object {
        private const val TAG = "GodotPushNotifications"
        private const val SIGNAL_DEVICE_TOKEN = "WillSendDeviceToken"
        private const val SIGNAL_REGISTRATION_ERROR = "RegistrationError"
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private var badgeNumber: Int = 0

    override fun getPluginName() = BuildConfig.GODOT_PLUGIN_NAME

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SignalInfo(SIGNAL_DEVICE_TOKEN, String::class.java),
            SignalInfo(SIGNAL_REGISTRATION_ERROR, String::class.java)
        )
    }

    @UsedByGodot
    fun registerPushNotifications() {
        val activity = activity ?: run {
            Log.e(TAG, "Activity is null")
            emitSignal(SIGNAL_REGISTRATION_ERROR, "Activity is not available")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val errorMsg = task.exception?.message ?: "Unknown error"
                Log.e(TAG, "Failed to get FCM token", task.exception)
                emitSignal(SIGNAL_REGISTRATION_ERROR, errorMsg)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d(TAG, "FCM Token: $token")
            emitSignal(SIGNAL_DEVICE_TOKEN, token)
        }
    }

    @UsedByGodot
    fun setBadgeNumber(value: Int) {
        badgeNumber = value
    }

    @UsedByGodot
    fun getBadgeNumber(): Int {
        return badgeNumber
    }
}
