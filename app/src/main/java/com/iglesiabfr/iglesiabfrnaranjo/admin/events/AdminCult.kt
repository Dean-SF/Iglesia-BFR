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

class AdminCult : AppCompatActivity() {
    private lateinit var eventSpinner: Spinner
    private lateinit var eventIds: List<String> // Lista de IDs de eventos
    private lateinit var selectedEventId: String // ID del evento seleccionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_cults)

        val createEventCultBut : Button = findViewById(R.id.createEventCultBut)
        val markAttendanceButt: Button = findViewById(R.id.MarkEventAttendanceCultBut)

        // Deshabilitar el botón markAttendanceButt inicialmente
        markAttendanceButt.isEnabled = false

        // Obtener referencias de UI
        eventSpinner = findViewById(R.id.eventSpinner)

        // Obtener la lista de IDs de eventos disponibles (de tu base de datos, por ejemplo)
        eventIds = getListOfEventIds()

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

        createEventCultBut.setOnClickListener {
            val i = Intent(this,CreateCultEvent::class.java)
            startActivity(i)
        }

        markAttendanceButt.setOnClickListener {
            val i = Intent(this,MarkAttendanceCults::class.java)
            intent.putExtra("eventId", intent.getStringExtra("eventId"))
            startActivity(i)
        }

        val backBtn: Button = findViewById(R.id.BackAdminEventCultButton)
        backBtn.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }
    }

    // Método para obtener la lista de IDs de eventos disponibles
    private fun getListOfEventIds(): List<String> {
        // Aquí obtienes la lista de IDs de eventos de tu base de datos
        // Por ahora, simplemente retornamos una lista vacía
        return emptyList()
    }
}