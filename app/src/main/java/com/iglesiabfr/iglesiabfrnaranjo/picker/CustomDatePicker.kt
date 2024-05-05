package com.iglesiabfr.iglesiabfrnaranjo.picker

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import java.util.TimeZone

class CustomDatePicker(fromToday : Boolean) {

    private var onPickListener: ((LocalDate,String) -> Unit)? = null

    private val datePicker : MaterialDatePicker<Long>
    init {
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(
                DateValidatorPointForward.now())
        var dateBuilder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.createDatePicker)
            .setTheme(R.style.ThemeOverlay_App_DatePicker)

        if(fromToday)
            dateBuilder = dateBuilder.setCalendarConstraints(constraintsBuilder.build())

        datePicker = dateBuilder.build()

        datePicker.addOnPositiveButtonClickListener {
            var sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val stringRep =  sdf.format(it)
            sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            onPickListener?.invoke(LocalDate.parse(sdf.format(it)),stringRep)
        }
    }

    fun setOnPickListener(listener : (LocalDate,String) -> Unit) : CustomDatePicker {
        onPickListener = listener
        return this
    }

    fun show(manager : FragmentManager) {
        datePicker.show(manager,"customDatePicker")
    }
}