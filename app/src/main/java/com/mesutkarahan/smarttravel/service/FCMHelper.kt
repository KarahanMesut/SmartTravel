package com.mesutkarahan.fotografpaylasim.model

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging

object FCMHelper {

    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var cameraPermissionLauncher: ActivityResultLauncher<String>? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var activityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var locationPermissionLauncher: ActivityResultLauncher<Array<String>>? = null

    fun initialize(
        fragment: Fragment,
        onPermissionGranted: () -> Unit,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        permissionLauncher: ActivityResultLauncher<String>,
        locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        this.activityResultLauncher = activityResultLauncher
        this.permissionLauncher = permissionLauncher
        this.locationPermissionLauncher = locationPermissionLauncher

        requestPermissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getToken { token ->
                    if (token != null) {
                        Log.w("FCMHelper", "Fetched FCM registration token: $token")
                    } else {
                        Log.w("FCMHelper", "Failed to fetch FCM registration token")
                    }
                    onPermissionGranted()
                }
            } else {
                Toast.makeText(fragment.requireContext(), "Bildirim izni reddedildi", Toast.LENGTH_SHORT).show()
            }
        }

        cameraPermissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openCamera(fragment)
            } else {
                Toast.makeText(fragment.requireContext(), "Kamera izni reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
        this.locationPermissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                getLastKnownLocation(fragment.requireContext())
            } else {
                Toast.makeText(fragment.requireContext(), "Konum izinleri reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getToken(callback: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCMHelper", "Fetching FCM registration token failed", task.exception)
                callback(null)
                return@OnCompleteListener
            }
            val token = task.result
            Log.w("FCMHelper", "Fetched FCM registration token: $token")
            callback(token)
        })
    }

    fun requestNotificationPermission(fragment: Fragment, onPermissionGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                getToken { token ->
                    if (token != null) {
                        Log.w("FCMHelper", "Fetched FCM registration token: $token")
                    } else {
                        Log.w("FCMHelper", "Failed to fetch FCM registration token")
                    }
                    onPermissionGranted()
                }
            } else if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(fragment.requireContext(), "Bildirim izni gereklidir", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                requestPermissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            getToken { token ->
                if (token != null) {
                    Log.w("FCMHelper", "Fetched FCM registration token: $token")
                } else {
                    Log.w("FCMHelper", "Failed to fetch FCM registration token")
                }
                onPermissionGranted()
            }
        }
    }

    fun requestCameraPermission(fragment: Fragment) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            openCamera(fragment)
        } else if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(fragment.requireContext(), "Kamera izni gereklidir", Toast.LENGTH_SHORT).show()
            cameraPermissionLauncher?.launch(Manifest.permission.CAMERA)
        } else {
            cameraPermissionLauncher?.launch(Manifest.permission.CAMERA)
        }
    }



    private fun openCamera(fragment: Fragment) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(fragment.requireActivity().packageManager) != null) {
            fragment.startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(fragment.requireContext(), "Kamera uygulaması bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestLocationPermission(fragment: Fragment) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(fragment.requireContext(), "Konum izni gereklidir", Toast.LENGTH_SHORT).show()
                locationPermissionLauncher?.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            } else {
                locationPermissionLauncher?.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        } else {
            getLastKnownLocation(fragment.requireContext())
        }
    }
     fun getLastKnownLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin verilmediyse işlemi durdur
            Toast.makeText(context, "Konum izinleri verilmemiş", Toast.LENGTH_SHORT).show()
            return
        }

        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null

        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }

        if (bestLocation != null) {
            Log.d("FCMHelper", "Last known location: ${bestLocation.latitude}, ${bestLocation.longitude}")
            Toast.makeText(context, "Konum: ${bestLocation.latitude}, ${bestLocation.longitude}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Konum bilgisi alınamadı", Toast.LENGTH_LONG).show()
        }
    }

    private const val CAMERA_REQUEST_CODE = 200
}
