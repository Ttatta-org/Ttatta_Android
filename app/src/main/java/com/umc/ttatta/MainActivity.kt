package com.umc.ttatta

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.umc.core.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userRepository: UserRepository
    private val viewModel: MainViewModel by viewModels()

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private lateinit var cameraPermissionRequester: ActivityResultLauncher<String>
    private lateinit var locationPermissionRequester: ActivityResultLauncher<String>
    private lateinit var mediaPermissionRequester: ActivityResultLauncher<String>

    private var imageUri: Uri? = null
    private val imageFileState = MutableStateFlow<File?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // enableDebugMode()
        setLaunchers()
        setStatusBarTransparent()
        setContent {
            val imageFile by imageFileState.collectAsState()

            MainApp(
                viewModel = viewModel,
                imageFile = imageFile,
                onPermissionRequiredInitially = {
                    if (!isLocationPermissionGranted) locationPermissionRequester.launch(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                },
                onImagePickerCalled = {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    imageFileState.value = null
                    imagePickerLauncher.launch(intent)
                },
                onCameraCalled = {
                    if (isCameraPermissionGranted) createImageUri()?.let { uri ->
                        imageUri = uri
                        imageFileState.value = null
                        cameraLauncher.launch(uri)
                    } else cameraPermissionRequester.launch(
                        android.Manifest.permission.CAMERA
                    )
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
                    android.Manifest.permission.ACCESS_MEDIA_LOCATION
                )
            }
        }

        mediaPermissionRequester = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            /* empty */
        }
    }

    private fun enableDebugMode() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!userRepository.isIdAlreadyOccupied(DebugConfig.ID)) {
                userRepository.join(
                    id = DebugConfig.ID,
                    password = DebugConfig.PASSWORD,
                    name = DebugConfig.NAME,
                    nickname = DebugConfig.NICKNAME,
                    email = DebugConfig.EMAIL
                )
            }

            userRepository.login(
                id = DebugConfig.ID,
                password = DebugConfig.PASSWORD
            )

            viewModel.checkLogin()
        }
    }
}

private fun ComponentActivity.setStatusBarTransparent() {
    window.apply {
        WindowCompat.setDecorFitsSystemWindows(this, false)
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}

private fun Activity.createImageUri(): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

private fun Activity.uriToFile(uri: Uri): File? {
    val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg") // 내부 캐시 디렉토리에 저장
    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream) // 스트림을 복사
            }
        }
        return file
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

private val Activity.isCameraPermissionGranted get() =
    checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

private val Activity.isLocationPermissionGranted get() =
    checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

private val Activity.isMediaPermissionGranted @RequiresApi(Build.VERSION_CODES.Q) get() =
    checkSelfPermission(android.Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED
