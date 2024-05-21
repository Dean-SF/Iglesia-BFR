package com.iglesiabfr.iglesiabfrnaranjo.calendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R

class FullCalendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_calendar)
        val fragTrans = supportFragmentManager.beginTransaction()
        fragTrans.replace(R.id.calendarFrame,CalendarFragment())
        fragTrans.commit()
    }
}