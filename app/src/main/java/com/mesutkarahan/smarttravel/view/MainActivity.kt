package com.mesutkarahan.smarttravel.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mesutkarahan.smarttravel.R
import com.mesutkarahan.smarttravel.databinding.ActivityMainBinding
import com.mesutkarahan.smarttravel.worker.TravelReminderWorker
import java.util.concurrent.TimeUnit
import android.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        createNotificationChannel()

        Log.d("MainActivity", "Enqueuing TravelReminderWorker")
        val workRequest = OneTimeWorkRequestBuilder<TravelReminderWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES) // 1 dakika gecikme ile çalışacak
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)



        auth = FirebaseAuth.getInstance()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        Log.d("MainActivity", "Checking if user is logged in")
        val activeUser = auth.currentUser
        if (activeUser != null) {
            Log.d("MainActivity", "User is logged in. Navigating to HomeFragment.")
            navController.navigate(R.id.homeFragment)
        } else {
            Log.d("MainActivity", "No user is logged in. Staying on Login/Register screen.")
        }

        NavigationUI.setupActionBarWithNavController(this, navController)



    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("travel_notifications", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}