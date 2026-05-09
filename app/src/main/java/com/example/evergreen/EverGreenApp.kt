package com.example.evergreen

import android.app.Application
import com.cloudinary.android.MediaManager

class EverGreenApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Cloudinary
        // TODO: Replace "YOUR_CLOUD_NAME" with your actual Cloudinary cloud name
        val config = mapOf(
            "cloud_name" to "YOUR_CLOUD_NAME",
            "secure" to true
        )
        MediaManager.init(this, config)
    }
}
