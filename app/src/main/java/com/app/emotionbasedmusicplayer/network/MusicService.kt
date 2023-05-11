package com.app.emotionbasedmusicplayer.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.app.emotionbasedmusicplayer.models.MusicInfo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MusicService(context: Context) {

    val queue = Volley.newRequestQueue(context)

    suspend fun getSong(emotion_query: String): List<MusicInfo> {
        return withContext(IO) {

            val list = mutableListOf<MusicInfo>()

            val job = Job()

            val url = "https://deezerdevs-deezer.p.rapidapi.com/search?q=$emotion_query song"
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, {
                val dataArray = it.getJSONArray("data")
                for (index in 0 until dataArray.length()) {
                    val singleItem = dataArray.getJSONObject(index)
                    val musicUri = singleItem.getString("preview")
                    val musicSmallThumbnail = singleItem.getJSONObject("artist").getString("picture_medium")
                    val musicBigThumbnail = singleItem.getJSONObject("artist").getString("picture_xl")
                    val musicTitle = singleItem.getString("title")
                    val model = MusicInfo(musicSmallThumbnail,musicBigThumbnail, musicTitle, musicUri)
                    list.add(model)
                }
                job.complete()
            }, {

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["X-RapidAPI-Key"] = "584544b71bmsh0170c56f8ac0bc5p10a917jsn2b5abd2deb57"
                    map["X-RapidAPI-Host"] = "deezerdevs-deezer.p.rapidapi.com"
                    return map
                }
            }
            queue.add(jsonObjectRequest)
            while (job.isCompleted == false) {
                delay(500L)
            }
            list
        }
    }
}