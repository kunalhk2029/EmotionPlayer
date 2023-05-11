package com.app.emotionbasedmusicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.emotionbasedmusicplayer.CaptureEmotion.EmotionCaptureService.Companion.subscribeTopic

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeTopic(this,"EmotionCaptureService")
    }
}