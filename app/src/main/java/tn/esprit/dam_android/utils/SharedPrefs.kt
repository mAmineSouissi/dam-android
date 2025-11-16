package tn.esprit.dam_android.utils

import android.content.Context
import androidx.core.content.edit

object SharedPrefs {
    private const val PREFS_NAME = "shadowguard_prefs"
    private const val KEY_DEVICE_IDENTIFIER = "device_identifier"
    // Keep KEY_DEVICE_ID for backward compatibility, but prefer deviceIdentifier
    private const val KEY_DEVICE_ID = "device_id"

    /**
     * Save device identifier (Android ID or UUID)
     * This is the primary identifier for all device operations
     */
    fun saveDeviceIdentifier(context: Context, deviceIdentifier: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_DEVICE_IDENTIFIER, deviceIdentifier)
        }
    }

    /**
     * Get device identifier (Android ID or UUID)
     * Use this for all device operations (scan, fetch device, etc.)
     */
    fun getDeviceIdentifier(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_DEVICE_IDENTIFIER, null)
    }

    /**
     * Save device MongoDB _id (kept for backward compatibility)
     * @deprecated Prefer using deviceIdentifier for all operations
     */
    @Deprecated("Use saveDeviceIdentifier instead. MongoDB _id is no longer needed.")
    fun saveDeviceId(context: Context, deviceId: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_DEVICE_ID, deviceId)
        }
    }

    /**
     * Get device MongoDB _id (kept for backward compatibility)
     * @deprecated Prefer using deviceIdentifier for all operations
     */
    @Deprecated("Use getDeviceIdentifier instead. MongoDB _id is no longer needed.")
    fun getDeviceId(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_DEVICE_ID, null)
    }

    /**
     * Clear all device information
     */
    fun clearDeviceInfo(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            remove(KEY_DEVICE_ID)
            remove(KEY_DEVICE_IDENTIFIER)
        }
    }
}