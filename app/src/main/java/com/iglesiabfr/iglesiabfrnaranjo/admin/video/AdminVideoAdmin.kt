package com.iglesiabfr.iglesiabfrnaranjo.admin.video

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddVideosAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityVideosAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class AdminVideoAdmin : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var binding: ActivityAddVideosAdminBinding
    private lateinit var binding1: ActivityVideosAdminBinding
    private lateinit var adapter: VideoAdapter
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVideosAdminBinding.inflate(layoutInflater)
        binding1 = ActivityVideosAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realm = DatabaseConnector.db

        binding1.btnAddVideos.setOnClickListener {
            // Set content view to binding after adding inventory schoolMaterial
            setContentView(binding.root)
        }

        binding1.BackVideosAdminButton.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        binding.btnAddVideo.setOnClickListener {
            createVideo()
        }

        binding.BackAddVideosAdminButton.setOnClickListener {
            setContentView(binding1.root)
            loadVideos() // Asegúrate de cargar los videos al volver
        }

        initRecyclerView()
        loadVideos() // Cargar los videos al inicio
    }

    private fun createVideo() {
        val title = binding.etTitle.text.toString()
        val url = binding.etUrl.text.toString()

        if (title.isNotEmpty() && url.isNotEmpty()) {
            val video = Video().apply {
                this.title = title
                this.url = url
            }

            // Luego de crear el objeto Video, lo guardamos en la base de datos
            saveVideoToDatabase(video)

            // Limpiar los EditText después de agregar el libro
            binding.etTitle.text.clear()
            binding.etUrl.text.clear()

        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveVideoToDatabase(video: Video) {
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    copyToRealm(video)
                }
            }.onSuccess {
                loadVideos() // Recargar videos después de agregar
                Toast.makeText(this@AdminVideoAdmin, "Video guardado correctamente", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@AdminVideoAdmin, "Error al guardar el video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteVideoFromDatabase(videoId: String) {
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    val video = this.query<Video>("_id == $0", ObjectId(videoId)).first().find()
                    video?.let {
                        delete(video)
                    }
                }
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    loadVideos() // Recargar videos después de eliminar
                    Toast.makeText(this@AdminVideoAdmin, "Video eliminado correctamente", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminVideoAdmin, "Error al eliminar el video", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadVideos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val videos = realm.query<Video>().find()
            withContext(Dispatchers.Main) {
                adapter.submitList(videos)
            }
        }
    }

    private fun initRecyclerView(){
        adapter = VideoAdapter(
            onClickListener = { video: Video -> onItemSelected(video) },
            onClickDelete = { position: Int -> onDeletedItem(position) }
        )
        binding1.recyclerVideos.layoutManager = llmanager
        binding1.recyclerVideos.adapter = adapter
    }

    private fun onItemSelected(video: Video) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
        startActivity(intent)
    }

    private fun onDeletedItem(position: Int) {
        val video = adapter.currentList[position]
        deleteVideoFromDatabase(video._id.toString())
    }
}





