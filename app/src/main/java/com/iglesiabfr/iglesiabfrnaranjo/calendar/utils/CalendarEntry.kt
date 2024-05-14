package com.iglesiabfr.iglesiabfrnaranjo.calendar.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.Activity
import com.iglesiabfr.iglesiabfrnaranjo.schema.Cult
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.LinkedList

//private typealias Airport = Flight.Airport
/*
data class Flight(
    val time: LocalDateTime,
    val departure: Airport,
    val destination: Airport,
    @ColorRes val color: Int,
) {
    data class Airport(val city: String, val code: String)
}*/

enum class CalendarEntryType {
    EVENT,
    CULT,
    ACT
}

sealed class CalendarEntry(open val type: CalendarEntryType) {
    data class EventCalendar(
        val name : String,
        val date : LocalDateTime,
        val desc : String,
        override val type: CalendarEntryType
    ) : CalendarEntry(type)

    data class CultCalendar (
        val date : LocalDateTime,
        val time : String,
        val name : String,
        val desc : String,
        override val type: CalendarEntryType
    ) : CalendarEntry(type)
}

fun getCurrentWeekFirstDay(): RealmInstant {
    val today = LocalDateTime.now()
    val dateTime = today.withHour(0).withMinute(0).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
    return RealmInstant.from(dateTime.toEpochSecond(ZoneOffset.UTC),0)
}
fun generateCultDates(list : LinkedList<CalendarEntry>, cult: Cult, context : Context) {
    val currentWeekFirstDay = getCurrentWeekFirstDay()
    val weekdays = context.resources.getStringArray(R.array.createCultWeekdays)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    val time = LocalDateTime.ofEpochSecond(cult.time.epochSeconds,0, ZoneOffset.UTC)
    val timeString = weekdays[cult.weekDay] + " " + time.format(formatter)
    val lastDayOfCurrentWeek = LocalDateTime.now().withHour(time.hour).withMinute(time.minute).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    val date = lastDayOfCurrentWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.of(cult.weekDay+1)))

    if(cult.time.epochSeconds < currentWeekFirstDay.epochSeconds) {
        list.add(
            CalendarEntry.CultCalendar(
                date,
                timeString,
                cult.name,
                cult.desc,
                CalendarEntryType.CULT
            )
        )
    }
    list.add(
        CalendarEntry.CultCalendar(
            date.plusWeeks(1),
            timeString,
            cult.name,
            cult.desc,
            CalendarEntryType.CULT
        )
    )
    list.add(
        CalendarEntry.CultCalendar(
            date.plusWeeks(2),
            timeString,
            cult.name,
            cult.desc,
            CalendarEntryType.CULT
        )
    )
}

val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mm a")

fun getCalendarData(context: Context): LinkedList<CalendarEntry> {
    val calendarList = LinkedList<CalendarEntry>()

    val eventsFound = DatabaseConnector.db.query<Event>().sort("date").find()
    val actsFound = DatabaseConnector.db.query<Activity>().sort("date").find()
    val cultsFound = DatabaseConnector.db.query<Cult>().sort("time").sort("weekDay").find()

    eventsFound.map { event ->
        calendarList.add(
            CalendarEntry.EventCalendar(
                event.name,
                LocalDateTime.ofEpochSecond(event.date.epochSeconds, 0, ZoneOffset.UTC),
                event.desc,
                CalendarEntryType.EVENT
            )
        )
    }

    actsFound.map { activity ->
        calendarList.add(CalendarEntry.EventCalendar(
                activity.name,
                LocalDateTime.ofEpochSecond(activity.date.epochSeconds,0, ZoneOffset.UTC),
                activity.desc,
                CalendarEntryType.ACT
            )
        )
    }

    cultsFound.map {cult ->
        generateCultDates(calendarList,cult,context)
    }

    return calendarList
}

    /*
fun generateFlights(): List<Flight> = buildList {
    val currentMonth = YearMonth.now()

    currentMonth.atDay(17).also { date ->
        add(
            Flight(
                date.atTime(14, 0),
                Airport("Lagos", "LOS"),
                Airport("Abuja", "ABV"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(21, 30),
                Airport("Enugu", "ENU"),
                Airport("Owerri", "QOW"),
                R.color.blue,
            ),
        )
    }

    currentMonth.atDay(22).also { date ->
        add(
            Flight(
                date.atTime(13, 20),
                Airport("Ibadan", "IBA"),
                Airport("Benin", "BNI"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(17, 40),
                Airport("Sokoto", "SKO"),
                Airport("Ilorin", "ILR"),
                R.color.blue,
            ),
        )
    }

    currentMonth.atDay(3).also { date ->
        add(
            Flight(
                date.atTime(20, 0),
                Airport("Makurdi", "MDI"),
                Airport("Calabar", "CBQ"),
                R.color.teal_700,
            ),
        )
    }

    currentMonth.atDay(12).also { date ->
        add(
            Flight(
                date.atTime(18, 15),
                Airport("Kaduna", "KAD"),
                Airport("Jos", "JOS"),
                R.color.blue,
            ),
        )
    }

    currentMonth.plusMonths(1).atDay(13).also { date ->
        add(
            Flight(
                date.atTime(7, 30),
                Airport("Kano", "KAN"),
                Airport("Akure", "AKR"),
                R.color.blue,
            ),
        )
        add(
            Flight(
                date.atTime(10, 50),
                Airport("Minna", "MXJ"),
                Airport("Zaria", "ZAR"),
                R.color.blue,
            ),
        )
    }

    currentMonth.minusMonths(1).atDay(9).also { date ->
        add(
            Flight(
                date.atTime(20, 15),
                Airport("Asaba", "ABB"),
                Airport("Port Harcourt", "PHC"),
                R.color.blue,
            ),
        )
    }
}

val flightDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm")
*/