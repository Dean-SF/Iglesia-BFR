package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iglesiabfr.iglesiabfrnaranjo.R

class VideoAdapter(
    val context: Context,
    val listsVideos: List<Video>
    ) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cvVideo = itemView.findViewById(R.id.cvVideo) as CardView
        val ivVideo = itemView.findViewById(R.id.ivVideo) as ImageView
        val tvTitulo = itemView.findViewById(R.id.tvTitulo) as TextView
        val tvUrl = itemView.findViewById(R.id.tvUrl) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_videos, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return listsVideos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = listsVideos[position]

        holder.cvVideo.setOnClickListener {
            val intent = Uri.parse(video.url).let {
                Intent(Intent.ACTION_VIEW, it)
            }

            context.startActivity(intent)
        }

        Glide
            .with(context)
            .load(video.miniatura)
            .centerInside()
            .into(holder.ivVideo)

        holder.tvTitulo.text = video.title
        holder.tvUrl.text = video.url
    }

}
