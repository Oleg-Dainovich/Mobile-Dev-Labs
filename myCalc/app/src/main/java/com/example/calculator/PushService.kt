package com.example.calculator

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("TAG", "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.e("TAG", "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.e("TAG", "Message Notification Body: ${it.body}")
        }
    }
}