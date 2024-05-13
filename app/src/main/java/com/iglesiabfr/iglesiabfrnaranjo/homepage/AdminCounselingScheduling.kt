package com.iglesiabfr.iglesiabfrnaranjo.homepage
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
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
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.schema.CounselingSession
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AdminCounselingScheduling : AppCompatActivity() {

    private var user : User? = null
    private lateinit var date: LocalDate
    private lateinit var time: LocalTime
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var confirmDialog: ConfirmDialog
    private lateinit var dateText: TextView
    private lateinit var timeText: TextView

    private var scheduleSessionIdString: String? = null
    private var scheduleSessionId: ObjectId? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_admin_scheduling)
        this.user = DatabaseConnector.getLogCurrent()
        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)

        val calendarBtn: ImageButton = findViewById(R.id.dateBut)
        val timeBtn: ImageButton = findViewById(R.id.timeBut)
        val scheduleBtn: Button = findViewById(R.id.scheduleBtn)

        dateText = findViewById(R.id.fechainput)
        timeText = findViewById(R.id.horainput)

        // Retrieve sessionId from intent
        scheduleSessionIdString = intent.getStringExtra("sessionId")
        scheduleSessionId = scheduleSessionIdString?.let { ObjectId(it) }

        val customTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTheme(R.style.ThemeOverlay_App_TimePicker)
            .setTitleText(R.string.createTimePicker)
            .build()

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        val customDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.createDatePicker)
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        customDatePicker.addOnPositiveButtonClickListener {
            val instant = Instant.ofEpochMilli(it)
            val utcDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
            val zoneDateTime = utcDateTime.atZone(ZoneId.systemDefault())
            date = zoneDateTime.toLocalDate()
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getDefault()
            dateText.text = sdf.format(Date.from(zoneDateTime.toInstant()))
            dateText.error = null
        }

        customTimePicker.addOnPositiveButtonClickListener {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            time = LocalTime.of(customTimePicker.hour, customTimePicker.minute)
            timeText.text = time.format(formatter)
            timeText.error = null
        }

        timeBtn.setOnClickListener {
            customTimePicker.show(supportFragmentManager, "tag")
        }

        timeText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                customTimePicker.show(supportFragmentManager, "tag")
                true
            } else {
                false
            }
        }

        calendarBtn.setOnClickListener {
            customDatePicker.show(supportFragmentManager, "tag")
        }

        dateText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                customDatePicker.show(supportFragmentManager, "tag")
                true
            } else {
                false
            }
        }

        scheduleBtn.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.counselingConfirm ))
                .setOnConfirmationListener {
                    scheduleSession()
                }
        }
    }

    private fun checkTime() : Boolean {
        if(!this::date.isInitialized) return true
        if(!this::time.isInitialized) return true
        if(date < LocalDate.now() && time <= LocalTime.now()) {
            timeText.error = getString(R.string.createTimeAlreadyPassed)
            return true
        }
        return false
    }

    private fun checkIfEmpty() : Boolean {
        var retval = false
        if (timeText.text.isEmpty()) {
            timeText.error = getString(R.string.createTimeEmpty)
            retval = true
        }

        if (dateText.text.isEmpty()) {
            dateText.error = getString(R.string.createDateEmpty)
            retval = true
        }
        return retval
    }

    private fun scheduleSession() {
        if (checkIfEmpty()) return
        if (checkTime()) return

        loadingDialog.startLoading()

        // Obtener la sesión que se seleccionó para agendar
        val userQuery = user?.let { DatabaseConnector.db.query<CounselingSession>("_id == $0", scheduleSessionId).find().firstOrNull() }

        lifecycleScope.launch {
            lifecycleScope.launch {
                runCatching {
                    DatabaseConnector.db.write {
                        if (userQuery != null) {
                            findLatest(userQuery)
                                ?.let {
                                    val datetime = LocalDateTime.of(date,time)
                                    it.sessionDateTime = RealmInstant.from(datetime.toEpochSecond(ZoneOffset.UTC), datetime.nano)
                                    it.scheduled = true
                                }
                        }
                    }
                }.onSuccess {
                    dateText.text = ""
                    timeText.text = ""
                    loadingDialog.stopLoading()
                    Toast.makeText(this@AdminCounselingScheduling,getString(R.string.counselingSuccessSchedule), Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure {
                    loadingDialog.stopLoading()
                    Toast.makeText(this@AdminCounselingScheduling,getString(R.string.counselingErrorSchedule), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }
}