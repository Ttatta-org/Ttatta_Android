package com.umc.ttatta

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
import com.umc.ttatta.intent.IntentManager
import com.umc.ttatta.intent.IntentType
import com.umc.ttatta.model.event.IntentEvent
import com.umc.ttatta.util.createImageUri
import com.umc.ttatta.util.isCameraPermissionGranted
import com.umc.ttatta.util.isLocationPermissionGranted
import com.umc.ttatta.util.isMediaPermissionGranted
import com.umc.ttatta.util.setStatusBarTransparent
import com.umc.ttatta.util.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private lateinit var cameraPermissionRequester: ActivityResultLauncher<String>
    private lateinit var locationPermissionRequester: ActivityResultLauncher<String>
    private lateinit var mediaPermissionRequester: ActivityResultLauncher<String>

    private var imageUri: Uri? = null
    private val imageFileState = MutableStateFlow<File?>(null)
    private val intentTypeState = MutableStateFlow<IntentType?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLaunchers()
        setStatusBarTransparent()
        setContent {
            val imageFile by imageFileState.collectAsState()
            val intentType by intentTypeState.collectAsState()

            MainApp(
                viewModel = viewModel,
                imageFile = imageFile,
                intentEvent = remember(intentType) {
                    intentType?.let { intentType ->
                        IntentEvent(
                            intentType = intentType,
                            onDismissed = { intentTypeState.value = null }
                        )
                    }
                },
                onPermissionRequiredInitially = {
                    if (!isLocationPermissionGranted) locationPermissionRequester.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                },
                onImagePickerCalled = {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    imageFileState.value = null
                    imagePickerLauncher.launch(intent)
                },
                onCameraCalled = {
                    if (isCameraPermissionGranted) createImageUri()?.let { uri ->
                        imageUri = uri
                        imageFileState.value = null
                        cameraLauncher.launch(uri)
                    } else cameraPermissionRequester.launch(
                        Manifest.permission.CAMERA
                    )
                },
            )
        }

        resolveIntent()
    }

    private fun setLaunchers() {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data!!
                imageUri = uri
                imageFileState.value = uriToFile(uri)
            }
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { output ->
            if (output) {
                imageFileState.value = imageUri?.let { uriToFile(it) }
            }
        }

        cameraPermissionRequester = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) createImageUri()?.let { uri ->
                imageUri = uri
                imageFileState.value = null
                cameraLauncher.launch(uri)
            }
        }

        locationPermissionRequester = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!isMediaPermissionGranted) mediaPermissionRequester.launch(
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                )
            }
        }

        mediaPermissionRequester = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            /* empty */
        }
    }

    private fun resolveIntent() {
        val intentType = IntentManager.getIntentType(intent) ?: return
        this.intentTypeState.value = intentType
    }
}
