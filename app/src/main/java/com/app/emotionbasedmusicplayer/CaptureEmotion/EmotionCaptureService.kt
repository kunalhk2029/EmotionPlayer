package com.app.emotionbasedmusicplayer.CaptureEmotion

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.emotionbasedmusicplayer.R
import com.app.emotionbasedmusicplayer.ui.emotiondetectorscreen.EmotionDetectorScreen.Companion.lastEmotionDetected
import com.app.emotionbasedmusicplayer.ui.emotiondetectorscreen.EmotionDetectorScreen.Companion.lastEmotionDetectedTimeStamp
import com.app.emotionbasedmusicplayer.ui.emotiondetectorscreen.EmotionDetectorScreen.Companion.latestEmotion
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class EmotionCaptureService() : FirebaseMessagingService() {

    companion object {
        fun subscribeTopic(context: Context, topic: String) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener {
//                Toast.makeText(context, "Subscribed $topic", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
//                Toast.makeText(context, "Failed to Subscribe $topic", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val emotion = message.data.get("emotion")

        if (emotion != null) {
            if (emotion == lastEmotionDetected || System.currentTimeMillis() < lastEmotionDetectedTimeStamp + 10000L) {
                println("8989589 conidtin failed.............")
                return
            }

            lastEmotionDetected = emotion
            lastEmotionDetectedTimeStamp = System.currentTimeMillis()

            CoroutineScope(IO).launch {
                latestEmotion.send(emotion)
            }
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createchannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(applicationContext, "1")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Emotion Deteced")
            .setContentText("$emotion")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(1, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createchannel(manager: NotificationManager) {
        val channel = NotificationChannel(
            "1", "Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)
    }
}