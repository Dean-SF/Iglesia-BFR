package com.iglesiabfr.iglesiabfrnaranjo.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.iglesiabfr.iglesiabfrnaranjo.R

class CalendarDetail : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_calendar)

        val name = intent.getStringExtra("name")
        val desc = intent.getStringExtra("desc")
        val day = intent.getStringExtra("day")

        val nameView : EditText = findViewById(R.id.nameinput)
        val descView : EditText = findViewById(R.id.descinput)
        val dayView : EditText = findViewById(R.id.dayinput)

        nameView.setText(name)
        descView.setText(desc)
        dayView.setText(day)


        nameView.setOnTouchListener { _, _ ->
            true
        }
        descView.setOnTouchListener { _, _ ->
            true
        }
        dayView.setOnTouchListener { _, _ ->
            true
        }

    }
}