package com.mesutkarahan.smarttravel.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mesutkarahan.smarttravel.R
import com.mesutkarahan.smarttravel.roomdb.TravelInfoDatabase
import com.mesutkarahan.smarttravel.view.MainActivity
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.Manifest
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale


class TravelReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("TravelReminderWorker", "doWork called")

        checkUpcomingTravels()


        val workRequest = OneTimeWorkRequestBuilder<TravelReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        return Result.success()
    }

    private fun checkUpcomingTravels() {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        // Örneğin, 1 gün içinde yaklaşan seyahatleri kontrol edelim
        val oneDayInMillis = TimeUnit.DAYS.toMillis(1)
        val upcomingTravelTime = currentTime + oneDayInMillis

        Log.d("TravelReminderWorker", "Current time: $currentTime")
        Log.d("TravelReminderWorker", "Upcoming travel time: $upcomingTravelTime")

        // Test tarihler
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val testDate1 = sdf.parse("01/07/2024")
        val testDate2 = sdf.parse("30/06/2024")
        val travelDateInMillis1 = testDate1?.time
        val travelDateInMillis2 = testDate2?.time

        Log.d("TravelReminderWorker", "Test date 1 in millis: $travelDateInMillis1")
        Log.d("TravelReminderWorker", "Test date 2 in millis: $travelDateInMillis2")

        if (travelDateInMillis1 in currentTime..upcomingTravelTime) {
            Log.d("TravelReminderWorker", "Test date 1 is in the range.")
        } else {
            Log.d("TravelReminderWorker", "Test date 1 is NOT in the range.")
        }

        if (travelDateInMillis2 in currentTime..upcomingTravelTime) {
            Log.d("TravelReminderWorker", "Test date 2 is in the range.")
        } else {
            Log.d("TravelReminderWorker", "Test date 2 is NOT in the range.")
        }

        val travelInfoDatabase = TravelInfoDatabase.getDatabase(applicationContext)
        val travelInfoDao = travelInfoDatabase.travelInfoDao()

        val travels = travelInfoDao.getAllTravelsSync()
        for (travel in travels) {
            Log.d("TravelReminderWorker", "Checking travel: ${travel.location}, travelDate: ${travel.travelDate}")

            if (travel.travelDate >= currentTime && travel.travelDate <= upcomingTravelTime) {
                Log.d("TravelReminderWorker", "Sending notification for travel: ${travel.location}")
                sendNotification("Yaklaşan Seyahat", "Yaklaşan seyahatiniz: ${travel.location}")
            } else {
                Log.d("TravelReminderWorker", "Not Sending notification for travel")
                Log.d("TravelReminderWorker", "Travel date: ${travel.travelDate}, Current time: $currentTime, Upcoming travel time: $upcomingTravelTime")
                Log.d("TravelReminderWorker", "Time difference: ${travel.travelDate - currentTime} milliseconds")
            }
        }
    }





    private fun sendNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // İzin yok, bildirim gönderme
            return
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(applicationContext, "travel_notifications")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, builder.build())
        }
    }
}