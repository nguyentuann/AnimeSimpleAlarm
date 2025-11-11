package com.app.base.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.base.components.CommonComponents
import com.app.base.local.db.AppPreferences
import com.app.base.utils.LogUtil
import org.koin.java.KoinJavaComponent.get

object PermissionHelper {

    private val appPrefs: AppPreferences by lazy { get(AppPreferences::class.java) }
    private const val REQUEST_CODE_NOTIFICATION = 10001

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permission = Manifest.permission.POST_NOTIFICATIONS

        when {
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {
                LogUtil.log("✅ Đã có quyền POST_NOTIFICATIONS")
                appPrefs.hasNotificationPermission = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE_NOTIFICATION)
            }

            else -> {
                // Chỉ show custom dialog nếu đã hỏi trước đó và vẫn chưa có quyền
                if (appPrefs.hasNotificationPermission) {
                    showEnableNotificationDialog(activity)
                } else {
                    // Lần đầu tiên: xin quyền hệ thống
                    ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE_NOTIFICATION)
                    // KHÔNG đánh dấu hasNotificationPermission ở đây
                }
            }
        }
    }

    private fun showEnableNotificationDialog(context: Context) {
        CommonComponents.confirmDialog(
            context = context,
            title = "Turn on notifications",
            message = "To receive alarms and reminders, please enable notification permission in settings.",
            onConfirm = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )
    }
}
