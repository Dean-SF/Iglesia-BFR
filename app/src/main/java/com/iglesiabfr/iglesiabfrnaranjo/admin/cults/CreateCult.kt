package com.iglesiabfr.iglesiabfrnaranjo.admin.cults

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.cults.spinnerAdapter.WeekdaySpinnerAdapter
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.schema.Cult
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CreateCult : AppCompatActivity() {
    private lateinit var time : LocalTime
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var launcher : ActivityResultLauncher<Intent>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cult)

        loadingDialog = LoadingDialog(this)

        val timeBut : ImageButton = findViewById(R.id.timeBut)
        val createBut : Button = findViewById(R.id.CreateCultBut)
        val backBtn: Button = findViewById(R.id.BackCultBtn)

        val timetext : TextView = findViewById(R.id.horainput)
        val nametext : EditText = findViewById(R.id.nameinput)

        val weekdaySpinner : Spinner = findViewById(R.id.weekday)

        weekdaySpinner.adapter = WeekdaySpinnerAdapter(
            this,
            resources.getStringArray(R.array.createCultWeekdays)
            )


        val customTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTheme(R.style.ThemeOverlay_App_TimePicker)
            .setTitleText(R.string.createTimePicker)
            .build()


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

        createBut.setOnClickListener {
            createCult()
        }

        backBtn.setOnClickListener {
            val i = Intent(this, Homepage::class.java)
            launcher.launch(i)
        }
    }

    private fun checkIfEmpty() : Boolean {
        val nametext : EditText = findViewById(R.id.nameinput)
        val desctext : EditText = findViewById(R.id.descinput)
        val timetext : EditText = findViewById(R.id.horainput)
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

        return retval
    }
    private fun createCult() {
        val nametext : TextView = findViewById(R.id.nameinput)
        val desctext : TextView = findViewById(R.id.descinput)
        val weekdaySpinner : Spinner = findViewById(R.id.weekday)
        val timetext : TextView = findViewById(R.id.horainput)
        if(checkIfEmpty()) return
        loadingDialog.startLoading()
        val datetime = LocalDateTime.of(LocalDate.now(),time)
        val cult = Cult().apply {
            name = nametext.text.toString()
            weekDay = weekdaySpinner.selectedItemPosition
            time = RealmInstant.from(datetime.toEpochSecond(ZoneOffset.UTC),0)
            desc = desctext.text.toString()
            cancelDate = RealmInstant.from(0,0)
        }
        val context = this
        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    copyToRealm(cult)
                }
            }.onSuccess {
                loadingDialog.stopLoading()
                nametext.text = ""
                desctext.text = ""
                timetext.text = ""
                Toast.makeText(context,getString(R.string.createCultSuccess),Toast.LENGTH_SHORT).show()
            }.onFailure {
                loadingDialog.stopLoading()
                Toast.makeText(context,getString(R.string.createCultFailed),Toast.LENGTH_SHORT).show()
            }
        }
    }
}