package com.leadpresence.exportsignature

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.leadpresence.exportsignature.ui.theme.JustMySignTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JustMySignTheme {  // ← Add this wrapper
                SignatureScreen()
            }
        }
    }
}
object StoragePermission {

    fun hasWritePermission(
        context: Context
    ): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            true

        } else {

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

        }

    }

    fun requestPermission(
        activity: Activity,
        launcher: ActivityResultLauncher<String>
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            launcher.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        }

    }
}
