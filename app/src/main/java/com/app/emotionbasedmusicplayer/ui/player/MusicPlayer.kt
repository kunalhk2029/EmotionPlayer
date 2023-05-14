package com.app.emotionbasedmusicplayer.ui.player

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.app.emotionbasedmusicplayer.R
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView


class MusicPlayer : Fragment(R.layout.fragment_music_player) {

    lateinit var player: ExoPlayer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireArguments().getBoolean("isMovie").let {
            if (!it){
                initExoPlayer()
                requireView().findViewById<ImageView>(R.id.musicThumbnail).let {
                    Glide.with(it).load(requireArguments().getString("thumbnail")).into(it)
                }
            }else{
                initWebPlayer()
            }
        }
    }

    private fun initWebPlayer() {
        val client =WebViewClient()
        requireView().findViewById<StyledPlayerView>(R.id.musicPlayer).visibility=View.GONE
        requireView().findViewById<WebView>(R.id.webview).apply {
            visibility=View.VISIBLE
            webViewClient=client
            settings.displayZoomControls=true
            settings.domStorageEnabled=true
            settings.javaScriptEnabled=true
            loadUrl(requireArguments().getString("url")!!)
        }
    }

        private fun initExoPlayer() {
        val mediaItem = MediaItem.fromUri(requireArguments().getString("url")!!)
        player = ExoPlayer.Builder(requireContext()).build().also {
            requireView().findViewById<StyledPlayerView>(R.id.musicPlayer)?.let { uiplayer ->
                uiplayer.showController()
                uiplayer.player = it
            }
            it.playWhenReady = true
            it.setMediaItem(mediaItem)
            it.prepare()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::player.isInitialized) {
            player.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) {
            player.release()
        }
    }
}