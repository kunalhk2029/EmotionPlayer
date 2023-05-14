package com.app.emotionbasedmusicplayer.models

data class MusicInfo(
    val small_thumbnail: String,
    val big_thumbnail: String,
    val title: String,
    val uri: String
)


data class MovieInfo(
    val title: String,
    val uri: String
)