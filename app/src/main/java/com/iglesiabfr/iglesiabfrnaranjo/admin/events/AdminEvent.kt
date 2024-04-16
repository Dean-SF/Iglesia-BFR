package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R

class AdminEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_eventos)

        val createButt : Button = findViewById(R.id.createEventBut)

        createButt.setOnClickListener {
            val i = Intent(this,CreateEvent::class.java)
            startActivity(i)
        }
    }
}