package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.admin.video.VideoFragmentAdapter
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.FragmentAdminVideoBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoPage : Fragment() {
    private lateinit var realm: Realm
    private lateinit var binding: FragmentAdminVideoBinding
    private lateinit var adapter: VideoFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realm = DatabaseConnector.db

        initRecyclerView()
        loadVideos()
    }

    private fun initRecyclerView() {
        adapter = VideoFragmentAdapter(
            onClickListener = { video: Video -> onItemSelected(video) }
        )
        binding.recyclerListVideos.layoutManager = LinearLayoutManager(context)
        binding.recyclerListVideos.adapter = adapter
    }

    private fun loadVideos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val videos = realm.query<Video>().find()
            withContext(Dispatchers.Main) {
                adapter.submitList(videos)
            }
        }
    }

    private fun onItemSelected(video: Video) {
        val url = video.url
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo abrir el video. URL inválida.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "URL del video no válida.", Toast.LENGTH_SHORT).show()
        }
    }
}
