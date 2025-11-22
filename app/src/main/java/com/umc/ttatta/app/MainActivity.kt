package com.umc.ttatta.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.umc.ttatta.app.intent.IntentManager
import com.umc.ttatta.app.intent.IntentType
import com.umc.ttatta.app.model.event.IntentEvent
import com.umc.ttatta.app.util.FileManager.createImageUri
import com.umc.ttatta.app.util.setStatusBarTransparent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.min
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>

    private var imageUri: Uri? = null
    private var imageLoadedCallback: ((Uri?) -> Unit)? = null
    private val intentTypeState = MutableStateFlow<IntentType?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLaunchers()
        resolveIntent()
        setStatusBarTransparent()
        setContent {
            val intentType by intentTypeState.collectAsState()

            MainApp(
                viewModel = viewModel,
                intentEvent = remember(intentType) {
                    intentType?.let { intentType ->
                        IntentEvent(
                            intentType = intentType,
                            onDismissed = { intentTypeState.value = null },
                        )
                    }
                },
                requestImagePicker = { callback ->
                    imageLoadedCallback = callback
                    imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                requestCamera = { callback ->
                    imageLoadedCallback = callback

                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        createImageUri()?.let { uri ->
                            imageUri = uri
                            cameraLauncher.launch(uri)
                        }
                    }
                },
            )
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val metrics = newBase?.resources?.displayMetrics

        if (metrics == null) {
            super.attachBaseContext(null)
            return
        }

        val screenWidth = metrics.widthPixels
        val designWidth = 390 * metrics.density

        val scaleFactor = min(1f, screenWidth / designWidth)

        val newConfiguration = Configuration(newBase.resources?.configuration).apply {
            this.fontScale = scaleFactor
            this.densityDpi = (metrics.densityDpi * scaleFactor).roundToInt()
        }

        applyOverrideConfiguration(newConfiguration)
        super.attachBaseContext(newBase)
    }

    private fun setLaunchers() {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )

                imageUri = uri
                imageLoadedCallback?.invoke(uri)
            }
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { output ->
            if (output) {
                imageUri?.let { imageLoadedCallback?.invoke(it) }
            }
        }

        cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                createImageUri()?.let { uri ->
                    imageUri = uri
                    cameraLauncher.launch(uri)
                }
            }
        }
    }

    private fun resolveIntent() {
        val intentType = IntentManager.getIntentType(intent) ?: return
        this.intentTypeState.value = intentType
    }
}
