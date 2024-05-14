package com.iglesiabfr.iglesiabfrnaranjo.calendar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.iglesiabfr.iglesiabfrnaranjo.R

class FullCalendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_calendar)
        val fragTrans = supportFragmentManager.beginTransaction()
        fragTrans.replace(R.id.calendarFrame,Example5Fragment())
        fragTrans.commit()
    }
}