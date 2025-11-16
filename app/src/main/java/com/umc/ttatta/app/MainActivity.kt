package com.umc.ttatta.app

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.umc.ttatta.app.intent.IntentManager
import com.umc.ttatta.app.intent.IntentType
import com.umc.ttatta.app.model.event.IntentEvent
import com.umc.ttatta.app.util.FileManager.createImageUri
import com.umc.ttatta.app.util.PermissionManager.checkPermissionAndTryRequest
import com.umc.ttatta.app.util.PermissionManager.isCameraPermissionGranted
import com.umc.ttatta.app.util.PermissionManager.isLocationPermissionGranted
import com.umc.ttatta.app.util.PermissionManager.isMediaPermissionGranted
import com.umc.ttatta.app.util.setStatusBarTransparent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

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

                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    imagePickerLauncher.launch(intent)
                },
                requestCamera = { callback ->
                    imageLoadedCallback = callback

                    CoroutineScope(Dispatchers.IO).launch {
                        if (!checkPermissionAndTryRequest(Manifest.permission.CAMERA)) return@launch

                        createImageUri()?.let { uri ->
                            imageUri = uri
                            cameraLauncher.launch(uri)
                        }
                    }
                },
            )
        }
    }

    private fun setLaunchers() {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data!!
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
    }

    private fun resolveIntent() {
        val intentType = IntentManager.getIntentType(intent) ?: return
        this.intentTypeState.value = intentType
    }
}
