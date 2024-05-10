package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.picker.CustomDatePicker
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class CreateEvent : AppCompatActivity() {
    private lateinit var date : LocalDate
    private lateinit var time : LocalTime
    private lateinit var loadingDialog : LoadingDialog

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        loadingDialog = LoadingDialog(this)

        val calendarBut : ImageButton = findViewById(R.id.dateBut)
        val timeBut : ImageButton = findViewById(R.id.timeBut)
        val createBut : Button = findViewById(R.id.createEventBut)

        val datetext : TextView = findViewById(R.id.fechainput)
        val timetext : TextView = findViewById(R.id.horainput)
        val nametext : EditText = findViewById(R.id.nameinput)


        val customTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTheme(R.style.ThemeOverlay_App_TimePicker)
            .setTitleText(R.string.createTimePicker)
            .build()

        val customDatePicker = CustomDatePicker(true)

        nametext.setOnEditorActionListener {_, action, _ ->
            return@setOnEditorActionListener when(action) {
                EditorInfo.IME_ACTION_NEXT -> {
                    findViewById<EditText>(R.id.descinput).requestFocus()
                    true
                }
                EditorInfo.IME_ACTION_DONE -> {
                    true
                }
                else -> false
            }
        }


        customDatePicker.setOnPickListener { pickedDate, dateString ->
            date = pickedDate
            datetext.text = dateString
            datetext.error = null
        }

        customTimePicker.addOnPositiveButtonClickListener {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            time = LocalTime.of(customTimePicker.hour,customTimePicker.minute)
            timetext.text = time.format(formatter)
            timetext.error = null
        }

        timeBut.setOnClickListener {
            customTimePicker.show(supportFragmentManager,"tag")
        }

        timetext.setOnTouchListener { _, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_DOWN -> {

                    customTimePicker.show(supportFragmentManager,"tag")
                }
                else ->{}
            }
            true
        }

        timetext.setOnClickListener {
            customTimePicker.show(supportFragmentManager,"tag")
        }

        calendarBut.setOnClickListener {
            customDatePicker.show(supportFragmentManager)
        }

        datetext.setOnTouchListener { _, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_DOWN -> {
                    customDatePicker.show(supportFragmentManager )
                }
                else ->{}
            }
            true
        }

        createBut.setOnClickListener {
            createEvent()
        }

    }

    private fun checkTime() : Boolean {
        if(!this::date.isInitialized) return true
        if(!this::time.isInitialized) return true
        val timetext : EditText = findViewById(R.id.horainput)
        if(date == LocalDate.now() && time <= LocalTime.now()) {
            timetext.error = getString(R.string.createTimeAlreadyPassed)
            return true
        }
        return false
    }

    private fun checkIfEmpty() : Boolean {
        val nametext : EditText = findViewById(R.id.nameinput)
        val desctext : EditText = findViewById(R.id.descinput)
        val timetext : EditText = findViewById(R.id.horainput)
        val datetext : TextView = findViewById(R.id.fechainput)
        var retval = false

        if (nametext.text.isEmpty()) {
            nametext.error = getString(R.string.createTextEmpty)
            retval = true
        }

        if (desctext.text.isEmpty()) {
            desctext.error = getString(R.string.createTextEmpty)
            retval = true
        }

        if (timetext.text.isEmpty()) {
            timetext.error = getString(R.string.createTimeEmpty)
            retval = true
        }

        if (datetext.text.isEmpty()) {
            datetext.error = getString(R.string.createDateEmpty)
            retval = true
        }

        return retval
    }
    private fun createEvent() {
        val nametext : TextView = findViewById(R.id.nameinput)
        val desctext : TextView = findViewById(R.id.descinput)
        val datetext : TextView = findViewById(R.id.fechainput)
        val timetext : TextView = findViewById(R.id.horainput)
        if(checkIfEmpty()) return
        if(checkTime()) return
        loadingDialog.startLoading()
        val datetime = LocalDateTime.of(date,time)
        val event = Event().apply {
            name = nametext.text.toString()
            date = RealmInstant.from(datetime.toEpochSecond(ZoneOffset.UTC),0)
            desc = desctext.text.toString()
        }
        val context = this
        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    copyToRealm(event)
                }
            }.onSuccess {
                loadingDialog.stopLoading()
                nametext.text = ""
                desctext.text = ""
                datetext.text = ""
                timetext.text = ""
                Toast.makeText(context,getString(R.string.createEventSuccess),Toast.LENGTH_SHORT).show()
            }.onFailure {
                loadingDialog.stopLoading()
                Toast.makeText(context,getString(R.string.createEventFailed),Toast.LENGTH_SHORT).show()
            }
        }
    }
}