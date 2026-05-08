package com.example.evergreen

import android.app.Application
import com.cloudinary.android.MediaManager

class EverGreenApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "YOUR_CLOUD_NAME", // User should replace this
            "secure" to true
        )
        MediaManager.init(this, config)
    }
}
