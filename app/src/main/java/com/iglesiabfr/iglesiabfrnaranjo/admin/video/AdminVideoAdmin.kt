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
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminVideoAdmin : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var binding: ActivityAddVideosAdminBinding
    private lateinit var binding1: ActivityVideosAdminBinding
    private lateinit var adapter: VideoAdapter
    private val llmanager = LinearLayoutManager(this)
    private var currentBinding = 0
    private lateinit var currentVideo: Video
    private var isUpdating = false

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
            createOrUpdateVideo()
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

    private fun createOrUpdateVideo() {
        val title = binding.etTitle.text.toString()
        val url = binding.etUrl.text.toString()

        if (title.isNotEmpty() && url.isNotEmpty()) {
            if (isUpdating) {
                // Actualizar material existente
                updateVideoFromDatabase(currentVideo, title, url)
            } else {
                val newVideo = Video().apply {
                    this.title = title
                    this.url = url
                }

                saveVideoToDatabase(newVideo)
            }

            // Limpiar los EditText después de agregar el libro
            binding.etTitle.text.clear()
            binding.etUrl.text.clear()

            // Cambiar a la vista principal
            setContentView(binding1.root)
            currentBinding = 0
            binding.btnAddVideo.setText("Guardar") // Reset the button text
            isUpdating = false // Resetear el estado de actualización
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
                setContentView(binding1.root) // Cambiar a la vista principal
                currentBinding = 0
            }.onFailure {
                Toast.makeText(this@AdminVideoAdmin, "Error al guardar el video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateVideoFromDatabase(video: Video, title: String, url: String) {
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    findLatest(video)?.apply {
                        this.title = title
                        this.url = url
                    }
                }
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    loadVideos() // Recargar materiales después de actualizar
                    Toast.makeText(this@AdminVideoAdmin, "Video actualizado correctamente", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminVideoAdmin, "Error al actualizar el video", Toast.LENGTH_SHORT).show()
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
            onClickUpdate = { video ->
                // Switch to the update view and populate fields
                setContentView(binding.root)
                currentBinding = 1
                binding.etTitle.setText(video.title)
                binding.etUrl.setText(video.url)
                binding.btnAddVideo.setText("Actualizar")
                isUpdating = true
                currentVideo = video
            },
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






