package com.yuyang.messi.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

object PermissionUtil {

    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean {
        val hasAllPermission = !permissionArray.any {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        return hasAllPermission
    }

    @JvmStatic
    fun showMissingPermissionDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("提示")
            .setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。")
            .setNegativeButton("拒绝") { dialog: DialogInterface?, which: Int -> }
            .setPositiveButton("设置") { dialog: DialogInterface?, which: Int ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.setData(Uri.parse("package:" + context.packageName))
                context.startActivity(intent)
            }
            .setCancelable(false)
            .show()
    }
}
