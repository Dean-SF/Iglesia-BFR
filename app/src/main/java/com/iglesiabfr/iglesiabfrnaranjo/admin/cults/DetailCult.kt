package com.iglesiabfr.iglesiabfrnaranjo.admin.cults

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.cults.spinnerAdapter.WeekdaySpinnerAdapter
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.schema.Cult
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class DetailCult : AppCompatActivity() {

    private lateinit var loadingDialog : LoadingDialog
    private lateinit var confirmDialog : ConfirmDialog

    private lateinit var launcher : ActivityResultLauncher<Intent>
    private lateinit var time : LocalTime

    private lateinit var  nametext : EditText
    private lateinit var  desctext : EditText
    private lateinit var  timetext : EditText

    private lateinit var weekdayS : Spinner

    private lateinit var timeBut : ImageButton

    private lateinit var cancelBut : Button
    private lateinit var modBut : Button
    private lateinit var delBut : Button
    private lateinit var backBut : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_cult)
        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)
        loadingDialog.startLoading()
        initUiVars()

        val markAttendanceButt: Button = findViewById(R.id.markEventAttendanceCultBut)

        val objectId = ObjectId(intent.getStringExtra("object_id")!!)

        val cultQuery = DatabaseConnector.db.query<Cult>("_id == $0",objectId).find()
        if(cultQuery.isEmpty()) {
            loadingDialog.stopLoading()
            Toast.makeText(this,getString(R.string.eventNotFound),Toast.LENGTH_SHORT).show()
            finish()
        }

        enableEditing(false)
        val cult = cultQuery[0]
        val datetime = LocalDateTime.ofEpochSecond(cult.time.epochSeconds,0, ZoneOffset.UTC)
        time = datetime.toLocalTime()
        setUIElementProperties()
        setCultText(cult)
        isCultCancelledProtocol(cult)

        modBut.setOnClickListener {
            if(modBut.text == getString(R.string.eventDetailsMod)) {
                modBut.text = getString(R.string.eventDetailsModAccept)
                delBut.isEnabled = false
                enableEditing(true)
                return@setOnClickListener
            }
            confirmDialog.confirmation(getString(R.string.cultDetailModAsk))
                .setOnConfirmationListener {
                    loadingDialog.startLoading()
                    modBut.text = getString(R.string.eventDetailsMod)
                    updateCult(cult)
                    delBut.isEnabled = true
                    enableEditing(false)
                }.setOnDenialListener {
                    modBut.text = getString(R.string.eventDetailsMod)
                    setCultText(cult)
                    delBut.isEnabled = true
                    enableEditing(false)
                }
        }

        delBut.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.cultDetailDelAsk))
                .setOnConfirmationListener {
                    loadingDialog.startLoading()
                    deleteCult(cult)
                }
        }

        cancelBut.setOnClickListener {
            if (cancelBut.text == getString(R.string.cultDetailCancelBut)) {
                confirmDialog.confirmation(getString(R.string.cultDetailCancelAsk))
                    .setOnConfirmationListener {
                        cancelCult(cult,true)
                    }

            } else {
                confirmDialog.confirmation(getString(R.string.cultDetailResumeAsk))
                    .setOnConfirmationListener {
                        cancelCult(cult,false)
                    }
            }

        }

        markAttendanceButt.setOnClickListener {
            val i = Intent(this, MarkAttendanceCults::class.java)
            launcher.launch(i)
        }

        backBut.setOnClickListener {
            val i = Intent(this, AdminCult::class.java)
            launcher.launch(i)
        }

        loadingDialog.stopLoading()
    }

    private fun deleteCult(cult: Cult) {
        val context = this
        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    findLatest(cult).also {
                        delete(it!!)
                    }
                }
            }.onSuccess {
                loadingDialog.stopLoading()
                finish()
                Toast.makeText(context,getString(R.string.cultDetailDelSucc),Toast.LENGTH_SHORT).show()
            }.onFailure {
                loadingDialog.stopLoading()
                Toast.makeText(context,getString(R.string.cultDetailDelFail),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCult(cult : Cult) {
        val context = this
        lifecycleScope.launch {
            runCatching {
                val datetime = LocalDateTime.of(LocalDate.now(),time)
                DatabaseConnector.db.write {
                    findLatest(cult).let {
                        it!!.name = nametext.text.toString()
                        it.desc = desctext.text.toString()
                        it.weekDay = weekdayS.selectedItemPosition
                        it.time = RealmInstant.from(datetime.toEpochSecond(ZoneOffset.UTC),0)
                    }
                }
            }.onSuccess {
                loadingDialog.stopLoading()
                Toast.makeText(context,getString(R.string.cultDetailModSucc),Toast.LENGTH_SHORT).show()
            }.onFailure {
                loadingDialog.stopLoading()
                Toast.makeText(context,getString(R.string.cultDetailModFail),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isCultCancelledProtocol(cult: Cult) {
        val datetime = LocalDateTime.ofEpochSecond(cult.cancelDate.epochSeconds,0, ZoneOffset.UTC)
        val cultCancelDate = datetime.toLocalDate()
        val currentDate = LocalDate.now()
        Log.d("iglesiaProtocol",currentDate.toString())
        Log.d("iglesiaProtocol",cultCancelDate.toString())

        if (areDatesInSameWeek(cultCancelDate,currentDate)) {
            cancelBut.setText(R.string.cultDetailResumeBut)
        }
    }

    private fun setCultText(cult : Cult) {
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        val datetime = LocalDateTime.ofEpochSecond(cult.time.epochSeconds,0, ZoneOffset.UTC)

        time = datetime.toLocalTime()

        nametext.setText(cult.name)
        desctext.setText(cult.desc)
        weekdayS.setSelection(cult.weekDay)
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

        weekdayS.adapter = WeekdaySpinnerAdapter(
            this,
            resources.getStringArray(R.array.createCultWeekdays)
        )

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
    }

    private fun initUiVars() {
        nametext = findViewById(R.id.nameinput)
        desctext = findViewById(R.id.descinput)
        timetext = findViewById(R.id.horainput)

        weekdayS = findViewById(R.id.weekday)

        timeBut = findViewById(R.id.timeBut)

        cancelBut = findViewById(R.id.detailCancelCultBut)
        modBut = findViewById(R.id.detailModCultBut)
        delBut = findViewById(R.id.detailDelCultBut)
    }

    private fun enableEditing(value : Boolean) {
        nametext.isFocusable = value
        nametext.isFocusableInTouchMode = value
        timetext.isFocusable = value
        timetext.isFocusableInTouchMode = value
        desctext.isFocusable = value
        desctext.isFocusableInTouchMode = value

        weekdayS.isEnabled = value
        timeBut.isEnabled = value
    }

    private fun areDatesInSameWeek(date1: LocalDate, date2: LocalDate): Boolean {
        val weekFields = WeekFields.of(Locale.getDefault())
        if (date1.year != date2.year) return false
        return date1.get(weekFields.weekOfWeekBasedYear()) == date2.get(weekFields.weekOfWeekBasedYear())
    }

    private fun cancelCult(cult : Cult, cancel : Boolean) {
        val context = this
        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    findLatest(cult).let {
                        if (cancel)
                            it!!.cancelDate = RealmInstant.from(LocalDateTime.now().toEpochSecond(
                                ZoneOffset.UTC),0)
                        else
                            it!!.cancelDate = RealmInstant.from(0,0)
                    }
                }
            }.onSuccess {
                loadingDialog.stopLoading()
                finish()
                val msg = if (cancel)
                    getString(R.string.cultDetailCancelSucc)
                else
                    getString(R.string.cultDetailResumeSucc)
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
            }.onFailure {
                loadingDialog.stopLoading()
                val msg = if (cancel)
                    getString(R.string.cultDetailCancelFail)
                else
                    getString(R.string.cultDetailResumeFail)
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
            }
        }
    }
}

