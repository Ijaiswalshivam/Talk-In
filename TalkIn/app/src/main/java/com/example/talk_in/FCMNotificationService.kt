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
            .setSmallIcon(resourceImage)

        // Extract data payload
        val senderUid = remoteMessage.data["senderUid"]
        val senderName = remoteMessage.data["senderName"]

        // Open ChatActivity when notification is clicked
        val resultIntent = Intent(this, ChatActivity::class.java).apply {
            putExtra("uid", senderUid)
            putExtra("name", senderName)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Add action for reply to notification
        val replyIntent = Intent(this, NotificationReceiver::class.java)
        replyIntent.action = "com.example.talk_in.REPLY_ACTION"
        replyIntent.putExtra("uid", senderUid)
        replyIntent.putExtra("name", senderName)
        val replyPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val remoteInput = androidx.core.app.RemoteInput.Builder(NotificationReceiver.KEY_REPLY)
            .setLabel("Type your message")
            .build()

        val action = NotificationCompat.Action.Builder(
            0,
            "Reply",
            replyPendingIntent
        ).addRemoteInput(remoteInput)
            .build()

        builder.setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.notification?.body))
            .addAction(action)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_MAX)

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
