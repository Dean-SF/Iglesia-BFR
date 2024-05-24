package com.iglesiabfr.iglesiabfrnaranjo.admin.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video

class VideoFragmentAdapter(
    private val onClickListener: (Video) -> Unit
) : RecyclerView.Adapter<VideoFragmentViewHolder>() {

    val videoList = mutableListOf<Video>()

    val currentList: List<Video>
        get() = videoList

    fun submitList(newList: List<Video>) {
        videoList.clear()
        videoList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoFragmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_fragment_videos_list, parent, false)
        return VideoFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoFragmentViewHolder, position: Int) {
        val item = videoList[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = videoList.size
}
