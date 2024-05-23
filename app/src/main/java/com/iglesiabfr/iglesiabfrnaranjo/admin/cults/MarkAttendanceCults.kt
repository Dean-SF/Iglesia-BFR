package com.iglesiabfr.iglesiabfrnaranjo.admin.cults

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.AttendanceCults
import com.iglesiabfr.iglesiabfrnaranjo.schema.EventData
import io.realm.kotlin.Realm
import io.realm.kotlin.types.RealmInstant

class MarkAttendanceCults : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var eventId: String // Id del evento seleccionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_cults)

        realm = DatabaseConnector.db

        // Obtener el id del evento seleccionado
        eventId = intent.getStringExtra("eventId") ?: ""

        // Guardar el registro de la asistencia
        val createAttendanceCultsButt: Button = findViewById(R.id.createAttendancecultBut)
        createAttendanceCultsButt.setOnClickListener {
            markAttendanceCult()
        }

        val backCultsBtn: Button = findViewById(R.id.backAttendancecultBtn)
        backCultsBtn.setOnClickListener {
            val intent = Intent(this, AdminCult::class.java)
            startActivity(intent)
        }
    }

    // Método para registrar asistencia a los eventos
    private fun markAttendanceCult() {
        val eventData = getEventData()

        // Aquí registras la asistencia en la base de datos Realm
        val attendance = AttendanceCults().apply {
            eventId = this@MarkAttendanceCults.eventId
            memberId = eventData.eventName // Puedes utilizar el nombre como ID de miembro
            timestamp = RealmInstant.now()
            eventPresent = eventData.eventPresent
        }

        // Aquí debes guardar la instancia de asistencia en tu base de datos Realm
        realm.writeBlocking  {
            copyToRealm(attendance)
        }
    }

    private fun getEventData(): EventData {
        val eventName = findViewById<TextView>(R.id.nameInputcult).text.toString()
        val eventDescription = findViewById<TextView>(R.id.isPersonPresentcult).text.toString()

        return EventData(eventName, eventDescription)
    }
}
