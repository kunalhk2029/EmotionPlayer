package com.app.emotionbasedmusicplayer.ui.emotiondetectorscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.emotionbasedmusicplayer.MainActivity
import com.app.emotionbasedmusicplayer.R
import com.app.emotionbasedmusicplayer.models.MusicInfo
import com.app.emotionbasedmusicplayer.network.FakeNetwrokGreneratedModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmotionDetectorScreen : Fragment(R.layout.fragment_emotion_detector_screen),
    SongAdapter.Interaction {

    lateinit var songAdapter: SongAdapter

    lateinit var viewModel: PreviewViewModel

    companion object {
        val latestEmotion: Channel<String> = Channel()
        var lastEmotionDetected = ""
        var lastEmotionDetectedTimeStamp = 0L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (requireActivity() as MainActivity).viewModel

        initSongAdapter()
        initRecyclerView()

        listenToLatestEmotions()


        viewModel.list.observe(viewLifecycleOwner) {
            lastEmotionDetected.let {
                handleEmotionBasedUi(it)
            }
            requireView().findViewById<RecyclerView>(R.id.song_rv).visibility = View.VISIBLE
            requireView().findViewById<ProgressBar>(R.id.pb).visibility = View.GONE
            songAdapter.submitList(it)
            it.forEach {
//                println("487398493 = " + it)
            }
        }
    }

    private fun listenToLatestEmotions() {
        lifecycleScope.launch {
//            detectEmotion("happy")
        }

        latestEmotion.receiveAsFlow().onEach {
            println("5985985 = " + it)
            detectEmotion(it)
        }.launchIn(lifecycleScope)
    }

    private suspend fun detectEmotion(emotion: String) {
        getSongs(emotion)
    }

    private fun initSongAdapter() {
        songAdapter = SongAdapter(this)
    }

    private fun initRecyclerView() {
        requireView().findViewById<RecyclerView>(R.id.song_rv).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun getSongs(emotion_query: String) {
        requireView().findViewById<RecyclerView>(R.id.song_rv).visibility = View.GONE
        requireView().findViewById<ProgressBar>(R.id.pb).visibility = View.VISIBLE
        handleEmotionBasedUi(emotion_query)
    }

    private fun handleEmotionBasedUi(emotion_query: String) {
        val emojiView = requireView().findViewById<ImageView>(R.id.emojiview)
        val rootView = requireView().rootView
        emojiView.setImageDrawable(null)
        requireView().findViewById<TextView>(R.id.detectedemotiontextview).apply {
            text = emotion_query + " Emotion Detected"
            isAllCaps = true
        }

        when (emotion_query) {

            "happy" -> {
                setColor(ContextCompat.getColor(requireContext(), R.color.happycolor), rootView)
                Glide.with(emojiView).load(R.drawable.happyemoji).into(emojiView)
                if (FakeNetwrokGreneratedModels.sadList.isNotEmpty())
                    lifecycleScope.launch {
                        delay(1500L)
                        viewModel.list.postValue(FakeNetwrokGreneratedModels.happyList)
                    }
            }
            "sad" -> {
                setColor(ContextCompat.getColor(requireContext(), R.color.sadcolor), rootView)
                Glide.with(emojiView).load(R.drawable.sademoji).into(emojiView)
                if (FakeNetwrokGreneratedModels.sadList.isNotEmpty())
                    lifecycleScope.launch {
                        delay(1500L)
                        viewModel.list.postValue(FakeNetwrokGreneratedModels.sadList)
                    }

            }
            "angry" -> {
                setColor(ContextCompat.getColor(requireContext(), R.color.angrycolor), rootView)
                Glide.with(emojiView).load(R.drawable.angryemoji).into(emojiView)
                if (FakeNetwrokGreneratedModels.sadList.isNotEmpty())
                    lifecycleScope.launch {
                        delay(1500L)
                        viewModel.list.postValue(FakeNetwrokGreneratedModels.angryList)
                    }
            }
            else -> {
            }
        }

    }

    private fun setColor(@ColorInt color: Int, rootView: View) {
        rootView.setBackgroundColor(color)
        if (isDark(color))
            requireView().findViewById<TextView>(R.id.detectedemotiontextview)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun isDark(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    override fun onItemSelected(position: Int, item: MusicInfo) {
        val bundle = Bundle().apply {
            putString("url", item.uri)
            putString("thumbnail", item.big_thumbnail)
        }
        findNavController().navigate(R.id.action_emotionDetectorScreen_to_musicPlayer, bundle)
    }

}