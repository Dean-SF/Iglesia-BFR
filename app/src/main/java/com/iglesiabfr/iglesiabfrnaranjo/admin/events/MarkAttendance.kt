package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.os.Bundle
import io.realm.kotlin.Realm
import android.app.DatePickerDialog
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.login.StartingPage
import com.iglesiabfr.iglesiabfrnaranjo.schema.Attendance
import com.iglesiabfr.iglesiabfrnaranjo.schema.EventData
import io.realm.kotlin.types.RealmInstant
import java.time.LocalDate

class MarkAttendance : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var eventId: String // Id del evento seleccionado

    override fun onCreate(savedInstanceState: Bundle?) {
        // Obtener el id del evento seleccionado
        eventId = intent.getStringExtra("eventId") ?: ""

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

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

    private fun getEventData(): EventData {
        val eventName = findViewById<TextView>(R.id.nameInput).text.toString()
        val eventPresent = findViewById<TextView>(R.id.descInput).text.toString()

        return EventData(eventName, eventPresent)
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

    // Método de ejemplo para obtener la lista de miembros que asistieron (simulado)
    private fun getMemberList(): List<String> {
        // Aquí deberías obtener la lista de miembros que asistieron al evento (simulado,
        // debería venir de la base de datos)
        return listOf("member1", "member2", "member3")
    }

}
