package com.example.talk_in

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMNotificationService : FirebaseMessagingService() {
    private lateinit var mNotificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Play notification sound
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.isLooping = false
        }

        // Vibrate the device
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(100, 300, 300, 300)
        v.vibrate(pattern, -1)

        val resourceImage = resources.getIdentifier(remoteMessage.notification?.icon, "drawable", packageName)

        val builder = NotificationCompat.Builder(this, "CHANNEL_ID")
        builder.setSmallIcon(resourceImage)

        // Open ChatActivity when notification is clicked
        val resultIntent = Intent(this, MainActivity::class.java)
        //resultIntent.putExtra("uid", remoteMessage.data["senderUid"])
        //resultIntent.putExtra("name", remoteMessage.data["senderName"])
        val pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentTitle(remoteMessage.notification?.title)
        builder.setContentText(remoteMessage.notification?.body)
        builder.setContentIntent(pendingIntent)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.notification?.body))
        builder.setAutoCancel (true)
        builder.priority = Notification.PRIORITY_MAX

        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(
                channelId,
                "Messages Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }

        mNotificationManager.notify(100, builder.build())
    }
}
