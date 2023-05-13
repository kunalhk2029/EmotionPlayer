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
                    val musicSmallThumbnail =
                        singleItem.getJSONObject("artist").getString("picture_medium")
                    val musicBigThumbnail =
                        singleItem.getJSONObject("artist").getString("picture_xl")
                    val musicTitle = singleItem.getString("title")
                    val model =
                        MusicInfo(musicSmallThumbnail, musicBigThumbnail, musicTitle, musicUri)
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


    suspend fun getTracksFromSpotify(emotion_query: String): List<MusicInfo> {
        return withContext(IO) {

            val songNamelist = if
                    (emotion_query=="sad") emotionOrganizedList.sadList
            else if (emotion_query=="happy") emotionOrganizedList.happyList


            else listOf()
            val job = Job()
            val list = mutableListOf<MusicInfo>()
            var lastRequestId=""
            val lastrequestSongName= songNamelist.last()
            songNamelist.forEach{
                delay(1000L)
                println("6459849 name of song request =" + it)
                val url = "https://spotify23.p.rapidapi.com/search/?q=${it}&type=tracks"
                val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                    println("6459849 name of song request1 response =" + it)
                    it.getJSONObject("tracks").let {
                        it.getJSONArray("items").let {array->
//                        for (i in 0 until array.length()) {
                            val i=0
                            array.getJSONObject(i).getJSONObject("data").let {
                                val trackId = it.getString("id")
                                println("655656  10= "+trackId)
                                val urll = "https://spotify23.p.rapidapi.com/tracks/?ids=$trackId"
                                val jsonObjectRequest = object : JsonObjectRequest(Method.GET, urll, null, {
                                    println("6459849 name of song request2 response =" + it)
                                    it.getJSONArray("tracks").getJSONObject(0).let {
                                        it.let {
                                            val musicUri = it.getString("preview_url")
                                            val albums = it.getJSONObject("album")
                                            val imageUri = albums.getJSONArray("images").getJSONObject(0).getString("url")

                                            val musicSmallThumbnail = imageUri
                                            val musicBigThumbnail = imageUri
                                            val musicTitle = it.getString("name")
                                            val model =
                                                MusicInfo(musicSmallThumbnail, musicBigThumbnail, musicTitle, musicUri)
                                            list.add(model)
                                        }
                                    }
                                }, {
                                    println("6459849 name of song request2 error =" + it)
                                }) {
                                    override fun getHeaders(): MutableMap<String, String> {
                                        val map = HashMap<String, String>()
                                        map["X-RapidAPI-Key"] = "243f7852c6mshca1e18f9437d784p12150ejsn7463129ba882"
                                        map["X-RapidAPI-Host"] = "spotify23.p.rapidapi.com"
                                        return map
                                    }
                                }
                               val requestId= queue.add(jsonObjectRequest)
//                                println("6459849 if block fired.....22. =" + lastrequestSongName)
//                                println("6459849 if block fired...... =" + url.contains(lastrequestSongName))
                                if (url.contains(lastrequestSongName)){
                                    println("6459849 if block fired...... =" + it)
                                    lastRequestId=requestId.url
                                }
                            }
//                        }
                        }
                    }
                }, {
                    println("6459849 name of song request1 error =" + it)

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val map = HashMap<String, String>()
                        map["X-RapidAPI-Key"] = "243f7852c6mshca1e18f9437d784p12150ejsn7463129ba882"
                        map["X-RapidAPI-Host"] = "spotify23.p.rapidapi.com"
                        return map
                    }
                }
               queue.add(jsonObjectRequest)
            }
            queue.addRequestEventListener { request, event ->
//                println("6459849 queue request =" + request.url)
//                println("6459849 queue event =" + event)
                if (request.url==lastRequestId) {
                    println("6459849 if block fired......1 2 =")
                    job.complete()
                }

            }
            while (job.isCompleted == false) {
                delay(500L)
            }
            println("6459849 = " + list)
            list
        }
    }
}