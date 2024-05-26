package com.iglesiabfr.iglesiabfrnaranjo.admin.cults

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAttendanceCultsBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityEventCultBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.AttendanceCults
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarkAttendanceCults : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var binding: ActivityAttendanceCultsBinding
    private lateinit var binding1: ActivityEventCultBinding
    private lateinit var adapter: EventCultAdapter
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceCultsBinding.inflate(layoutInflater)
        binding1 = ActivityEventCultBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_attendance)

        realm = DatabaseConnector.db

        // Guardar el registro de la asistencia
        binding.createAttendanceCultBut.setOnClickListener {
            markAttendanceCult()
        }

        binding.backAttendanceCultBtn.setOnClickListener {
            setContentView(binding1.root)
            loadAttendancesCults() // Asegúrate de cargar a las personas presentes al volver
        }

        initRecyclerView()
        loadAttendancesCults() // Cargar a las personas presentes al inicio
    }

    // Método para registrar asistencia a los eventos
    private fun markAttendanceCult() {
        val name = binding.nameInputCult.text.toString()

        // Aquí registras la asistencia en la base de datos Realm
        if (name.isNotEmpty()) {
            val attendanceCults = AttendanceCults().apply {
                namePerson = name
                timestamp = RealmInstant.now()
            }

            lifecycleScope.launch {
                // Luego de crear el objeto Video, lo guardamos en la base de datos
                saveMarkAttendanceCultToDatabase(attendanceCults)

                // Limpiar los EditText después de agregar la asistencia
                binding.nameInputCult.text.clear()
            }
        }
    }

    private suspend fun saveMarkAttendanceCultToDatabase(attendanceCults: AttendanceCults) {
        withContext(Dispatchers.IO) {
            try {
                realm.write {
                    copyToRealm(attendanceCults)
                }
                withContext(Dispatchers.Main) {
                    loadAttendancesCults()
                    Toast.makeText(this@MarkAttendanceCults, "Persona presente guardada correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MarkAttendanceCults, "Error al guardar la persona presente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteMarkAttendanceCultFromDatabase(attendanceCults: AttendanceCults) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    realm.write {
                        findLatest(attendanceCults).also {
                            delete(it!!)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        loadAttendancesCults()
                        Toast.makeText(this@MarkAttendanceCults, "Persona presente eliminado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MarkAttendanceCults, "Error al eliminar a la persona presente", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadAttendancesCults() {
        lifecycleScope.launch(Dispatchers.IO) {
            val attendancesCults = realm.query<AttendanceCults>().find()
            withContext(Dispatchers.Main) {
                adapter.submitList(attendancesCults)
            }
        }
    }

    private fun initRecyclerView(){
        adapter = EventCultAdapter(
            onClickListener = { attendanceCults: AttendanceCults -> onItemSelected(attendanceCults) },
            onClickDelete = { position: Int -> onDeletedItem(position) }
        )
        binding1.recyclerEventsCults.layoutManager = llmanager
        binding1.recyclerEventsCults.adapter = adapter
    }

    private fun onItemSelected(attendanceCults: AttendanceCults) {
        val url = attendanceCults.namePerson
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
        val attendanceCults = adapter.currentList[position]
        deleteMarkAttendanceCultFromDatabase(attendanceCults)
        adapter.notifyItemRemoved(position)
    }
}
