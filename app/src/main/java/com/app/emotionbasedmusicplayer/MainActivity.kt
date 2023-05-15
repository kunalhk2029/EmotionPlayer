package com.app.emotionbasedmusicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.app.emotionbasedmusicplayer.CaptureEmotion.EmotionCaptureService.Companion.subscribeTopic
import com.app.emotionbasedmusicplayer.ui.emotiondetectorscreen.PreviewViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: PreviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeTopic(this,"EmotionCaptureService")
    }
}