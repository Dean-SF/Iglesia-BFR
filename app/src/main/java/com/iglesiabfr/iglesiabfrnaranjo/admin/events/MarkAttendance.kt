package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.Attendance
import com.iglesiabfr.iglesiabfrnaranjo.schema.EventData
import io.realm.kotlin.Realm
import io.realm.kotlin.types.RealmInstant

class MarkAttendance : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var eventId: String // Id del evento seleccionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        // Obtener el id del evento seleccionado
        eventId = intent.getStringExtra("eventId") ?: ""

        // Guardar el registro de la asistencia
        val createAttendanceButt: Button = findViewById(R.id.createAttendanceBut)
        createAttendanceButt.setOnClickListener {
            markAttendance()
        }

        val backBtn: Button = findViewById(R.id.backAttendanceBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, AdminEvent::class.java)
            startActivity(intent)
        }
    }

    // Método para registrar asistencia a los eventos
    private fun markAttendance() {
        val eventData = getEventData()

        // Aquí registras la asistencia en la base de datos Realm
        val attendance = Attendance().apply {
            eventId = this@MarkAttendance.eventId
            memberId = eventData.eventName // Puedes utilizar el nombre como ID de miembro
            timestamp = RealmInstant.now()
            eventPresent = eventData.eventPresent
            // Aquí puedes agregar más campos según sea necesario
        }

        // Aquí debes guardar la instancia de asistencia en tu base de datos Realm
        realm.writeBlocking  {
            copyToRealm(attendance)
        }
    }

    private fun getEventData(): EventData {
        val eventName = findViewById<TextView>(R.id.nameInput).text.toString()
        val eventDescription = findViewById<TextView>(R.id.descInput).text.toString()

        return EventData(eventName, eventDescription)
    }
}
