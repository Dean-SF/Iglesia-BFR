package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAttendanceBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityEventBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.Attendance
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarkAttendance : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var binding1: ActivityEventBinding
    private lateinit var adapter: EventAdapter
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        binding1 = ActivityEventBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_attendance)

        realm = DatabaseConnector.db

        // Guardar el registro de la asistencia
        binding.createAttendanceBut.setOnClickListener {
            markAttendance()
        }

        binding.BackPresentEventsButton.setOnClickListener {
            setContentView(binding1.root)
            loadAttendances() // Asegúrate de cargar los videos al volver
        }

        initRecyclerView()
        loadAttendances() // Cargar los videos al inicio
    }

    // Método para registrar asistencia a los eventos
    private fun markAttendance() {
        val name = binding.nameInput.text.toString()

        // Aquí registras la asistencia en la base de datos Realm
        if (name.isNotEmpty()) {
            val attendance = Attendance().apply {
                namePerson = name
                timestamp = RealmInstant.now()
            }

            lifecycleScope.launch {
                // Luego de crear el objeto Video, lo guardamos en la base de datos
                saveMarkAttendanceToDatabase(attendance)

                // Limpiar los EditText después de agregar la asistencia
                binding.nameInput.text.clear()
            }
        }
    }

    private suspend fun saveMarkAttendanceToDatabase(attendance: Attendance) {
        withContext(Dispatchers.IO) {
            try {
                realm.write {
                    copyToRealm(attendance)
                }
                withContext(Dispatchers.Main) {
                    loadAttendances()
                    Toast.makeText(this@MarkAttendance, "Persona presente guardada correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MarkAttendance, "Error al guardar la persona presente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteMarkAttendanceFromDatabase(attendance: Attendance) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    realm.write {
                        findLatest(attendance).also {
                            delete(it!!)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        loadAttendances()
                        Toast.makeText(this@MarkAttendance, "Persona presente eliminado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MarkAttendance, "Error al eliminar a la persona presente", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadAttendances() {
        lifecycleScope.launch(Dispatchers.IO) {
            val attendances = realm.query<Attendance>().find()
            withContext(Dispatchers.Main) {
                adapter.submitList(attendances)
            }
        }
    }

    private fun initRecyclerView(){
        adapter = EventAdapter(
            onClickListener = { attendance: Attendance -> onItemSelected(attendance) },
            onClickDelete = { position: Int -> onDeletedItem(position) }
        )
        binding1.recyclerEvents.layoutManager = llmanager
        binding1.recyclerEvents.adapter = adapter
    }

    private fun onItemSelected(attendance: Attendance) {
        val url = attendance.namePerson
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
        val attendance = adapter.currentList[position]
        deleteMarkAttendanceFromDatabase(attendance)
        adapter.notifyItemRemoved(position)
    }
}
