package com.app.emotionbasedmusicplayer.ui.emotiondetectorscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.emotionbasedmusicplayer.R
import com.app.emotionbasedmusicplayer.models.MusicInfo
import com.app.emotionbasedmusicplayer.network.MusicService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EmotionDetectorScreen : Fragment(R.layout.fragment_emotion_detector_screen),
    SongAdapter.Interaction {

    lateinit var songAdapter: SongAdapter

    companion object {
        val latestEmotion: Channel<String> = Channel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSongAdapter()
        initRecyclerView()

        listenToLatestEmotions()

    }

    private fun listenToLatestEmotions() {
        latestEmotion.receiveAsFlow().onEach {
            println("5985985 = " + it)
            detectEmotion(it)
        }.launchIn(lifecycleScope)
    }

    private suspend fun detectEmotion(emotion: String) {
        println("5985985 = 1 =" + emotion)
        when (emotion) {
            "Happy" -> {
                getSongs("happy").let {
                    songAdapter.submitList(it)
                }
            }
            "Sad" -> {
                getSongs("sad").let {
                    songAdapter.submitList(it)
                }
            }
            "Angry" -> {
                getSongs("angry").let {
                    songAdapter.submitList(it)
                }
            }
            else->{
                getSongs(emotion).let {
                    songAdapter.submitList(it)
                }
            }
        }
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
    private suspend fun getSongs(emotion_query: String): List<MusicInfo> {
        requireView().findViewById<RecyclerView>(R.id.song_rv).visibility = View.GONE
        requireView().findViewById<ProgressBar>(R.id.pb).visibility = View.VISIBLE
        requireView().findViewById<TextView>(R.id.detectedemotiontextview).apply {
            text = emotion_query + " Emotion Detected"
            isAllCaps = true
        }

        val musicitemlist = MusicService(requireContext()).getTracksFromSpotify(emotion_query)

        requireView().findViewById<RecyclerView>(R.id.song_rv).visibility = View.VISIBLE
        requireView().findViewById<ProgressBar>(R.id.pb).visibility = View.GONE

        return musicitemlist
    }

    override fun onItemSelected(position: Int, item: MusicInfo) {
        val bundle = Bundle().apply {
            putString("url", item.uri)
            putString("thumbnail", item.big_thumbnail)
        }
        findNavController().navigate(R.id.action_emotionDetectorScreen_to_musicPlayer, bundle)
    }

}