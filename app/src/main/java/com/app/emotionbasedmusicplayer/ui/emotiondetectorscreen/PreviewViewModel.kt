package com.app.emotionbasedmusicplayer.ui.emotiondetectorscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.emotionbasedmusicplayer.models.MovieInfo
import com.app.emotionbasedmusicplayer.models.MusicInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel
@Inject constructor():ViewModel() {

    val list = MutableLiveData<List<MusicInfo>>()
    val movieList = MutableLiveData<List<MovieInfo>>()
}