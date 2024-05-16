package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import io.realm.kotlin.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AdminEvent : AppCompatActivity() {
    private lateinit var eventSpinner: Spinner
    private lateinit var eventIds: List<String> // Lista de IDs de eventos
    private lateinit var selectedEventId: String // ID del evento seleccionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_eventos)

        val createButt : Button = findViewById(R.id.createEventBut)
        val markAttendanceButt: Button = findViewById(R.id.markEventAttendanceBut)

        // Deshabilitar el botón markAttendanceButt inicialmente
        markAttendanceButt.isEnabled = false

        // Obtener referencias de UI
        eventSpinner = findViewById(R.id.eventSpinner)

        // Obtener la lista de IDs de eventos disponibles (de tu base de datos, por ejemplo)
        getListOfEventIds()

        // Crear un ArrayAdapter para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventIds)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el ArrayAdapter al Spinner
        eventSpinner.adapter = adapter

        // Manejar la selección del usuario
        eventSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEventId = eventIds[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Deshabilitar el botón markAttendanceButt
                markAttendanceButt.isEnabled = false
            }
        }

        createButt.setOnClickListener {
            val i = Intent(this,CreateEvent::class.java)
            startActivity(i)
        }

        markAttendanceButt.setOnClickListener {
            val i = Intent(this,MarkAttendance::class.java)
            intent.putExtra("eventId", intent.getStringExtra("eventId"))
            startActivity(i)
        }

        val backBtn: Button = findViewById(R.id.backAdminEventButton)
        backBtn.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }
    }

    // Método para obtener la lista de IDs de eventos disponibles
    private fun getListOfEventIds() {
        GlobalScope.launch(Dispatchers.Main) {
            val realm = Realm.getDefaultInstance()
            val events = realm.where(Event::class.java).findAll()
            eventIds = events.map { it._id.toHexString() }
            realm.close()

            // Crear un ArrayAdapter para el Spinner
            val adapter = ArrayAdapter(this@AdminEvent, android.R.layout.simple_spinner_item, eventIds)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Asignar el ArrayAdapter al Spinner
            eventSpinner.adapter = adapter

            // Manejar la selección del usuario
            eventSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedEventId = eventIds[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Deshabilitar el botón markAttendanceButt
                    markAttendanceButt.isEnabled = false
                }
            }
        }
    }
}

