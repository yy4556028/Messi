package com.yuyang.messi.ui.media.camerax

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.extensions.*

class MyExtenderHelper {

    fun setPreviewExtender(builder: Preview.Builder, cameraSelector: CameraSelector) {
        BeautyPreviewExtender.create(builder).let {
            if (it.isExtensionAvailable(cameraSelector)) {
                // Enable the extension if available.
                Log.d("MyExtenderHelper", "beauty preview extension enable")
                it.enableExtension(cameraSelector)
            } else {
                Log.d("MyExtenderHelper", "beauty preview extension not available")
            }
        }
    }

    fun setCaptureExtender(builder: ImageCapture.Builder, cameraSelector: CameraSelector) {
        val nightImageCaptureExtender = NightImageCaptureExtender.create(builder)
        if (nightImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            Log.d("MyExtenderHelper", "night capture extension enable")
            nightImageCaptureExtender.enableExtension(cameraSelector)
        } else {
            Log.d("MyExtenderHelper", "night capture extension not available")
        }

        val bokehImageCapture = BokehImageCaptureExtender.create(builder);
        if (bokehImageCapture.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            Log.d("MyExtenderHelper", "hdr extension enable");
            bokehImageCapture.enableExtension(cameraSelector);
        } else {
            Log.d("MyExtenderHelper", "hdr extension not available");
        }

        val hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            Log.d("MyExtenderHelper", "night extension enable");
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        } else {
            Log.d("MyExtenderHelper", "night extension not available");
        }

        val beautyImageCaptureExtender = BeautyImageCaptureExtender.create(builder);
        if (beautyImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            Log.d("MyExtenderHelper", "beauty extension enable");
            beautyImageCaptureExtender.enableExtension(cameraSelector);
        } else {
            Log.d("MyExtenderHelper", "beauty extension not available");
        }
    }
}