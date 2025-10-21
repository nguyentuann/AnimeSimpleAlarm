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
            // ✅ Đã có quyền
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                LogUtil.log("✅ Đã có quyền POST_NOTIFICATIONS")
                appPrefs.hasNotificationPermission = true
            }

            // ❌ Từng từ chối, nhưng chưa chọn “Don’t ask again”
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    REQUEST_CODE_NOTIFICATION
                )
            }

            else -> {
                // ⚙️ Kiểm tra xem đã xin quyền lần đầu chưa
                if (!appPrefs.hasNotificationPermission) {
                    // 👉 Lần đầu: xin quyền
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(permission),
                        REQUEST_CODE_NOTIFICATION
                    )
                    appPrefs.hasNotificationPermission = true // đánh dấu đã hỏi rồi
                } else {
                    // 👉 Đã hỏi rồi mà vẫn chưa có quyền → user chọn “Don’t ask again”
                    showEnableNotificationDialog(activity)
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
