/*package com.iglesiabfr.iglesiabfrnaranjo.admin.video

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.homepage.VideoPage
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video

class VisualizationVideoPage : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter

    private var videoUrls: ArrayList<String>? = null
    private var videoTitles: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_video, container, false)
        recyclerView = view.findViewById(R.id.recyclerListVideos)

        videoUrls = arguments?.getStringArrayList("videoUrls")
        videoTitles = arguments?.getStringArrayList("videoTitles")

        videoUrls?.let { urls ->
            videoTitles?.let { titles ->
                val videos = urls.zip(titles).map { Video().apply { url = it.first; title = it.second } }
                adapter = VideoAdapter(
                    onClickListener = { video -> onVideoClicked(video.url) },
                    onClickDelete = {} // Empty lambda
                )
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
                adapter.submitList(videos)
            }
        }

        return view
    }

    private fun onVideoClicked(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir el video. URL inválida.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(videoUrls: ArrayList<String>, videoTitles: ArrayList<String>): VideoPage {
            val fragment = VideoPage()
            val args = Bundle()
            args.putStringArrayList("videoUrls", videoUrls)
            args.putStringArrayList("videoTitles", videoTitles)
            fragment.arguments = args
            return fragment
        }
    }
}*/