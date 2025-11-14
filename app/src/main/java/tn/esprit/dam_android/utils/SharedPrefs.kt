package tn.esprit.dam_android.utils

import android.content.Context
import androidx.core.content.edit

object SharedPrefs {
    private const val PREFS_NAME = "shadowguard_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    fun saveDeviceId(context: Context, deviceId: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_DEVICE_ID, deviceId)
        }
    }

    fun getDeviceId(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_DEVICE_ID, null)
    }
}