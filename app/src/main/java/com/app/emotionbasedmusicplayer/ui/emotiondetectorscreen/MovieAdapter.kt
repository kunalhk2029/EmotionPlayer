package com.app.emotionbasedmusicplayer.ui.emotiondetectorscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.emotionbasedmusicplayer.R
import com.app.emotionbasedmusicplayer.models.MovieInfo
import com.bumptech.glide.Glide

class MovieAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieInfo>() {

        override fun areItemsTheSame(oldItem: MovieInfo, newItem: MovieInfo): Boolean {
            return oldItem.uri == newItem.uri
        }

        override fun areContentsTheSame(oldItem: MovieInfo, newItem: MovieInfo): Boolean {
            return oldItem.title == newItem.title
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MusicItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.song_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MusicItemViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<MovieInfo>) {
        differ.submitList(list)
    }

    class MusicItemViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: MovieInfo) = with(itemView) {

            findViewById<TextView>(R.id.songname).let {
                it.text=item.title
            }

            findViewById<ImageView>(R.id.lgr).let {
                Glide.with(it).load(R.drawable.ic_baseline_movie_24).circleCrop().into(it)
            }
            itemView.setOnClickListener {
                interaction?.onItemSelected(bindingAdapterPosition, item)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: MovieInfo)
    }
}