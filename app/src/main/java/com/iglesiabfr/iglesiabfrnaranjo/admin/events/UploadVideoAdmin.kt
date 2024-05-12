package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityListRecordingBinding

class UploadVideoAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityListRecordingBinding
    private lateinit var adapter: VideoAdapter

    val listVideos = ArrayList<Video>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRecordingBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_list_recording)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvVideos.layoutManager = LinearLayoutManager(this)
        adapter = VideoAdapter(this, listVideos)
        binding.rvVideos.adapter = adapter
    }

    fun datos() {
        // Aca debo de pedir la información de los videos
    }
}






