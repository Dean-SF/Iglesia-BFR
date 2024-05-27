package com.iglesiabfr.iglesiabfrnaranjo.admin.notifHandler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.messaging
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector

class NotifHandler : FirebaseMessagingService() {
    companion object {
        var updateToken : ((String) -> (Unit))? = null
        @JvmStatic
        fun initNotifs(context: Context) {
            val channelId = context.resources.getString(R.string.NotifId)
            val channelName = context.resources.getString(R.string.NotifName)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ),
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken?.invoke(token)
        Firebase.messaging.unsubscribeFromTopic("admin")
        if(DatabaseConnector.getIsAdmin()) {
            Firebase.messaging.subscribeToTopic("admin")
        }
        Firebase.messaging.unsubscribeFromTopic("updates")
        Firebase.messaging.subscribeToTopic("updates")
            .addOnSuccessListener {
                Log.d("Mes","wedidittoken")
            }
            .addOnFailureListener {
                Log.d("Mes","wenotdidittoken")
            }
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("sis","sis")
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {notif ->
            notif.title?.let { notif.body?.let { it1 -> sendNotification(it, it1) } }
        }
    }

    private fun sendNotification(title:String,messageBody: String) {

        val channelId = getString(R.string.NotifId)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_church)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)

            .setSound(defaultSoundUri)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val channel = NotificationChannel(
            channelId,
            resources.getString(R.string.NotifName),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)


        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}