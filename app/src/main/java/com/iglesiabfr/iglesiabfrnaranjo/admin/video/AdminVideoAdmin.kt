package com.iglesiabfr.iglesiabfrnaranjo.admin.video

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddVideosAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityVideosAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.FragmentAdminVideoBinding
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.homepage.VideoPage
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
    private var currentBinding = 0
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVideosAdminBinding.inflate(layoutInflater)
        binding1 = ActivityVideosAdminBinding.inflate(layoutInflater)
        setContentView(binding1.root)
        currentBinding = 0

        realm = DatabaseConnector.db

        binding1.btnAddVideos.setOnClickListener {
            // Set content view to binding after adding video
            setContentView(binding.root)
            currentBinding = 1
        }

        binding.btnAddVideo.setOnClickListener {
            createVideo()
        }

        initRecyclerView()
        loadVideos() // Cargar los videos al inicio
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        if(currentBinding == 1) {
            currentBinding = 0
            setContentView(binding1.root)
            loadVideos()
            return
        }
        super.onBackPressed()
    }

    private fun createVideo() {
        val title = binding.etTitle.text.toString()
        val url = binding.etUrl.text.toString()

        if (title.isNotEmpty() && url.isNotEmpty()) {
            val video = Video().apply {
                this.title = title
                this.url = url
            }

            lifecycleScope.launch {
                // Luego de crear el objeto Video, lo guardamos en la base de datos
                saveVideoToDatabase(video)

                // Limpiar los EditText después de agregar el libro
                binding.etTitle.text.clear()
                binding.etUrl.text.clear()
            }
        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveVideoToDatabase(video: Video) {
        withContext(Dispatchers.IO) {
            try {
                realm.write {
                    copyToRealm(video)
                }
                withContext(Dispatchers.Main) {
                    loadVideos()
                    Toast.makeText(this@AdminVideoAdmin, "Video guardado correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminVideoAdmin, "Error al guardar el video", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteVideoFromDatabase(video: Video) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    realm.write {
                        findLatest(video).also {
                            delete(it!!)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        loadVideos()
                        Toast.makeText(this@AdminVideoAdmin, "Video eliminado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AdminVideoAdmin, "Error al eliminar el video", Toast.LENGTH_SHORT).show()
                    }
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
        val url = video.url
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir el video. URL inválida.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "URL del video no válida.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDeletedItem(position: Int) {
        val video = adapter.currentList[position]
        deleteVideoFromDatabase(video)
        adapter.notifyItemRemoved(position)
    }
}






