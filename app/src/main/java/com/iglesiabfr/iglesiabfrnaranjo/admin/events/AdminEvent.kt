package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Adminpage
import com.iglesiabfr.iglesiabfrnaranjo.schema.Attendance
import io.realm.kotlin.Realm
import io.realm.kotlin.types.RealmInstant

class AdminEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_eventos)

        val createButt : Button = findViewById(R.id.createEventBut)

        createButt.setOnClickListener {
            val i = Intent(this,CreateEvent::class.java)
            startActivity(i)
        }

        val markAttendanceButt: Button = findViewById(R.id.markEventAttendanceBut)

        markAttendanceButt.setOnClickListener {
            val i = Intent(this,CreateEvent::class.java)
            startActivity(i)
        }

        val backBtn: Button = findViewById(R.id.backAdminEventButton)
        backBtn.setOnClickListener {
            val intent = Intent(this, Adminpage::class.java)
            startActivity(intent)
        }
    }
}