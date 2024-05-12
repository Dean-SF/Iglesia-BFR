package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import java.time.LocalDate

class CreateCultEvent : AppCompatActivity() {

    private lateinit var date : LocalDate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cult)

        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            Log.d("IglesiaInfo","adsad")
            date = LocalDate.of(year,month,dayOfMonth)
            updateDate()
        }

        val calendarBut : ImageButton = findViewById(R.id.dateBut)
        calendarBut.setOnClickListener {
            DatePickerDialog(this).show()
        }

        val backBtn: Button = findViewById(R.id.BackEventBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, AdminCult::class.java)
            startActivity(intent)
        }
    }

    private fun updateDate() {
        val datetext : TextView = findViewById(R.id.fechaInput)
        datetext.text = date.toString()
        datetext.text = "23"
        Log.d("IglesiaInfo","ejecuto")
    }
}