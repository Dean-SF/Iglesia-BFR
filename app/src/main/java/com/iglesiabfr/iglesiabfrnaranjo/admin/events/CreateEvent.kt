package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DatePickerDialog
import com.iglesiabfr.iglesiabfrnaranjo.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar

class CreateEvent : AppCompatActivity() {

    private lateinit var date : LocalDate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            Log.d("IglesiaInfo","adsad")
            date = LocalDate.of(year,month,dayOfMonth)
            updateDate()
        }

        val calendarBut : ImageButton = findViewById(R.id.dateBut)
        calendarBut.setOnClickListener {
            DatePickerDialog(this).show()
        }

    }

    private fun updateDate() {
        val datetext : TextView = findViewById(R.id.fechainput)
        datetext.text = date.toString()
        datetext.text = "23"
        Log.d("IglesiaInfo","ejecuto")
    }
}