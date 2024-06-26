package com.iglesiabfr.iglesiabfrnaranjo.admin.video

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemFragmentVideosListBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video

class VideoFragmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemFragmentVideosListBinding.bind(view)

    fun render(
        videoModel: Video,
        onClickListener: (Video) -> Unit
    ) {
        binding.tvTitulo.text = videoModel.title
        binding.tvUrl.text = videoModel.url

        // Get YouTube video ID
        val videoId = getYouTubeVideoId(videoModel.url)
        // Construct thumbnail URL
        val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"

        // Load thumbnail using Glide
        Glide.with(binding.ivVideo.context)
            .load(thumbnailUrl)
            .into(binding.ivVideo)

        itemView.setOnClickListener { onClickListener(videoModel) }
    }

    private fun getYouTubeVideoId(url: String): String {
        val uri = Uri.parse(url)
        return uri.getQueryParameter("v") ?: uri.pathSegments.last()
    }
}
