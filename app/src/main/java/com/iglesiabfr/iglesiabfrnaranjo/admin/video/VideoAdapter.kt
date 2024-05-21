package com.iglesiabfr.iglesiabfrnaranjo.admin.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video

class VideoAdapter(
    private val onClickListener: (Video) -> Unit,
    private val onClickDelete:(Int) -> Unit
) : RecyclerView.Adapter<VideoViewHolder>() {

    val videoList = mutableListOf<Video>()

    val currentList: List<Video>
        get() = videoList

    fun submitList(newList: List<Video>) {
        videoList.clear()
        videoList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return VideoViewHolder(layoutInflater.inflate(R.layout.item_rv_videos_list, parent, false))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = videoList[position]
        holder.render(item, onClickListener, onClickDelete)
    }

    override fun getItemCount(): Int = videoList.size
}

