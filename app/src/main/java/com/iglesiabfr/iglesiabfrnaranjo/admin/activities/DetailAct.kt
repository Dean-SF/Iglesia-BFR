package com.iglesiabfr.iglesiabfrnaranjo.admin.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.schema.Activity
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone

class DetailAct : AppCompatActivity() {

    private lateinit var loadingDialog : LoadingDialog
    private lateinit var confirmDialog : ConfirmDialog

    private lateinit var date : LocalDate
    private lateinit var time : LocalTime

    private lateinit var  nametext : EditText
    private lateinit var  desctext : EditText
    private lateinit var  timetext : EditText
    private lateinit var  datetext : EditText

    private lateinit var calendarBut : ImageButton
    private lateinit var timeBut : ImageButton

    private lateinit var modBut : Button
    private lateinit var delBut : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_act)
        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)
        loadingDialog.startLoading()
        initUiVars()

        val objectId = ObjectId(intent.getStringExtra("object_id")!!)

        val actQuery = DatabaseConnector.db.query<Activity>("_id == $0",objectId).find()
        if(actQuery.isEmpty()) {
            loadingDialog.stopLoading()
            Toast.makeText(this,getString(R.string.actNotFound),Toast.LENGTH_SHORT).show()
            finish()
        }

        enableEditing(false)
        val act = actQuery[0]
        setActText(act)
        setUIElementProperties()

        modBut.setOnClickListener {
            if(modBut.text == getString(R.string.eventDetailsMod)) {
                modBut.text = getString(R.string.eventDetailsModAccept)
                delBut.isEnabled = false
                enableEditing(true)
                return@setOnClickListener
            }
            confirmDialog.confirmation(getString(R.string.actDetailModAsk))
                .setOnConfirmationListener {
                    loadingDialog.startLoading()
                    modBut.text = getString(R.string.eventDetailsMod)
                    updateAct(act)
                    delBut.isEnabled = true
                    enableEditing(false)
                }.setOnDenialListener {
                    modBut.text = getString(R.string.eventDetailsMod)
                    setActText(act)
                    delBut.isEnabled = true
                    enableEditing(false)
                }
        }

        delBut.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.actDetailDelAsk))
                .setOnConfirmationListener {
                    loadingDialog.startLoading()
                    deleteAct(act)
                }
        }


        loadingDialog.stopLoading()
    }

    private fun deleteAct(act: Activity) {
        val context = this
        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    findLatest(act).also {
                        delete(it!!)
                    }
                }
            }.onSuccess {
                loadingDialog.stopLoading()
                finish()
                Toast.makeText(context,getString(R.string.actDetailDelSucc),Toast.LENGTH_SHORT).show()
            }.onFailure {
                loadingDialog.stopLoading()
                Toast.makeText(context,getString(R.string.actDetailDelFail),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAct(act : Activity) {
        val context = this
        lifecycleScope.launch {
            runCatching {
                val datetime = LocalDateTime.of(date,time)
                DatabaseConnector.db.write {
                    findLatest(act).let {
                        it!!.name = nametext.text.toString()
                        it.desc = desctext.text.toString()
                        it.date = RealmInstant.from(datetime.toEpochSecond(ZoneOffset.UTC),0)
                    }
                }
            }.onSuccess {
                loadingDialog.stopLoading()
                Toast.makeText(context,getString(R.string.actDetailModSucc),Toast.LENGTH_SHORT).show()
            }.onFailure {
                loadingDialog.stopLoading()
                Toast.makeText(context,getString(R.string.actDetailModFail),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setActText(act : Activity) {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        val datetime = LocalDateTime.ofEpochSecond(act.date.epochSeconds,0, ZoneOffset.UTC)

        date = datetime.toLocalDate()
        time = datetime.toLocalTime()

        nametext.setText(act.name)
        desctext.setText(act.desc)
        datetext.setText(datetime.format(dateFormatter))
        timetext.setText(datetime.format(timeFormatter))
    }

    private fun setUIElementProperties() {
        nametext.setOnEditorActionListener {_, action, _ ->
            return@setOnEditorActionListener when(action) {
                EditorInfo.IME_ACTION_NEXT -> {
                    desctext.requestFocus()
                    true
                }
                EditorInfo.IME_ACTION_DONE -> {
                    true
                }
                else -> false
            }
        }
        setDateAndTimePicker()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDateAndTimePicker() {


        val customTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(time.hour)
            .setMinute(time.minute)
            .setTheme(R.style.ThemeOverlay_App_TimePicker)
            .setTitleText(R.string.createTimePicker)
            .build()

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(
                DateValidatorPointForward.now())

        val customDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.createDatePicker)
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .setSelection(LocalDateTime.of(date,time).truncatedTo(ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC)*1000)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        customDatePicker.addOnPositiveButtonClickListener {
            var sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            date = LocalDate.parse(sdf.format(it))
            sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            datetext.setText(sdf.format(it))
            datetext.error = null
        }

        customTimePicker.addOnPositiveButtonClickListener {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            time = LocalTime.of(customTimePicker.hour,customTimePicker.minute)
            timetext.setText(time.format(formatter))
            timetext.error = null
        }

        timeBut.setOnClickListener {
            customTimePicker.show(supportFragmentManager,"tag")
        }

        timetext.setOnTouchListener { _, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_DOWN -> {
                    if (delBut.isEnabled) return@setOnTouchListener  true
                    customTimePicker.show(supportFragmentManager,"tag")
                }
                else ->{}
            }
            true
        }

        calendarBut.setOnClickListener {
            customDatePicker.show(supportFragmentManager,"tag")
        }

        datetext.setOnTouchListener { _, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_DOWN -> {
                    if (delBut.isEnabled) return@setOnTouchListener  true
                    customDatePicker.show(supportFragmentManager,"tag")
                }
                else ->{}
            }
            true
        }
    }

    private fun initUiVars() {
        nametext = findViewById(R.id.nameinput)
        desctext = findViewById(R.id.descinput)
        timetext = findViewById(R.id.horainput)
        datetext = findViewById(R.id.fechainput)

        calendarBut = findViewById(R.id.dateBut)
        timeBut = findViewById(R.id.timeBut)

        modBut = findViewById(R.id.detailModActBut)
        delBut = findViewById(R.id.detailDelActBut)
    }

    private fun enableEditing(value : Boolean) {
        nametext.isFocusable = value
        nametext.isFocusableInTouchMode = value
        timetext.isFocusable = value
        timetext.isFocusableInTouchMode = value
        datetext.isFocusable = value
        datetext.isFocusableInTouchMode = value
        desctext.isFocusable = value
        desctext.isFocusableInTouchMode = value
        calendarBut.isEnabled = value
        timeBut.isEnabled = value
    }
}