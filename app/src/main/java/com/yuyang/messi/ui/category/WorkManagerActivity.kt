package com.yuyang.messi.ui.category

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import com.yuyang.lib_base.ui.base.BaseActivity
import com.yuyang.messi.databinding.ActivityWorkmanagerBinding

class WorkManagerActivity1:BaseActivity() {
    private val pickPictureCallback = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null)
            Log.e(TAG, "Invalid input image Uri.")
        else
            Log.e(TAG, "Uri.")
//            startActivity(FilterActivity.newIntent(this, uri))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWorkmanagerBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
    }

    companion object {

        private const val TAG = "WorkManagerActivity"
        private const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"

        private const val MAX_NUMBER_REQUEST_PERMISSIONS = 2
        private const val REQUEST_CODE_PERMISSIONS = 101

        // A list of permissions the application needs.
        @VisibleForTesting
        val sPermissions: MutableList<String> = object : ArrayList<String>() {
            init {
                add(Manifest.permission.INTERNET)
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        private fun fromHtml(input: String): Spanned {
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                Html.fromHtml(input, Html.FROM_HTML_MODE_COMPACT)
            } else {
                // method deprecated at API 24.
                @Suppress("DEPRECATION")
                Html.fromHtml(input)
            }
        }
    }
}