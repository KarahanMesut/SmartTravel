package com.mesutkarahan.fotografpaylasim.model

import com.google.firebase.messaging.FirebaseMessagingService
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // Bu belirteci sunucunuza gönderme işlemini burada yapın
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // Token'ı sunucuya gönderme kodu burada olacak
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}