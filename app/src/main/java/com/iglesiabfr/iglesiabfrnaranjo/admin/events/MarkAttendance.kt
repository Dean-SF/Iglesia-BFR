package com.iglesiabfr.iglesiabfrnaranjo.admin.events

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
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class MarkAttendance : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var binding1: ActivityEventBinding
    private lateinit var adapter: EventAdapter
    private val llmanager = LinearLayoutManager(this)

    private lateinit var eventId: ObjectId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        binding1 = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Realm
        val config = RealmConfiguration.Builder(schema = setOf(Attendance::class))
            .name("attendance.realm")
            .build()
        realm = Realm.open(config)

        val objectId = ObjectId(intent.getStringExtra("object_id")!!)

        val eventQuery = DatabaseConnector.db.query<Event>("_id == $0",objectId).find()
        if(eventQuery.isEmpty()) {
            Toast.makeText(this,getString(R.string.eventNotFound),Toast.LENGTH_SHORT).show()
            finish()
        }

        val event = eventQuery[0]

        binding1.btnAddPersonsPresent.setOnClickListener {
            // Set content view to binding1 after adding new person
            setContentView(binding.root)
        }

        realm = DatabaseConnector.db

        // Guardar el registro de la asistencia
        binding.createAttendanceBut.setOnClickListener {
            markAttendance(event)
        }

        binding.BackPresentEventsButton.setOnClickListener {
            setContentView(binding1.root)
            loadAttendances() // Asegúrate de cargar los videos al volver
        }

        initRecyclerView()
        loadAttendances() // Cargar los videos al inicio
    }

    // Método para registrar asistencia a los eventos
    private fun markAttendance(event : Event) {
        val name = binding.nameInput.text.toString()

        // Aquí registras la asistencia en la base de datos Realm
        if (name.isNotEmpty()) {
            val attendance = Attendance().apply {
                namePerson = name
                timestamp = RealmInstant.now()
                eventId = event._id // Associate the attendance with the event
            }

            lifecycleScope.launch {
                // Luego de crear el objeto de presentes, lo guardamos en la base de datos
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
                        findLatest(attendance)?.let { delete(it) }
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
            val attendances = realm.query<Attendance>("eventId == $0", eventId).find() // Query attendances for the specific event
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
        Toast.makeText(this, attendance.namePerson, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position: Int) {
        val attendance = adapter.currentList[position]
        deleteMarkAttendanceFromDatabase(attendance)
        adapter.notifyItemRemoved(position)
    }
}
