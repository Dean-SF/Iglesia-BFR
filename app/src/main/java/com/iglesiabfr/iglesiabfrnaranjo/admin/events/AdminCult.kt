package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage

class AdminCult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_cults)

        val createEventCultBut : Button = findViewById(R.id.createEventCultBut)

        createEventCultBut.setOnClickListener {
            val i = Intent(this,CreateCultEvent::class.java)
            startActivity(i)
        }

        val markAttendanceButt: Button = findViewById(R.id.MarkEventAttendanceCultBut)

        markAttendanceButt.setOnClickListener {
            val i = Intent(this,CreateCultEvent::class.java)
            startActivity(i)
        }

        val backBtn: Button = findViewById(R.id.BackAdminEventCultButton)
        backBtn.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }
    }
}