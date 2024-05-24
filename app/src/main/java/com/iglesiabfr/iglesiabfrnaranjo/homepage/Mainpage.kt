package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.emotions.SeeEmotions
import com.iglesiabfr.iglesiabfrnaranjo.admin.suggestions.SuggestionsMailbox
import com.iglesiabfr.iglesiabfrnaranjo.calendar.CalendarDetail
import com.iglesiabfr.iglesiabfrnaranjo.calendar.FullCalendar
import com.iglesiabfr.iglesiabfrnaranjo.calendar.utils.dateFormatter
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.EagNextListA
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.items.EagItemA
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.emotions.SendEmotion
import com.iglesiabfr.iglesiabfrnaranjo.schema.Activity
import com.iglesiabfr.iglesiabfrnaranjo.schema.Cult
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import com.iglesiabfr.iglesiabfrnaranjo.suggestions.SendSuggestion
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Mainpage.newInstance] factory method to
 * create an instance of this fragment.
 */
class Mainpage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var popupMenu: PopupMenu? = null
    private var isSubMenuShowing: Boolean = false
    private lateinit var nextEvents : RecyclerView
    private lateinit var nextCults : RecyclerView
    private lateinit var nextActs : RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mainpage, container, false)

        val profBut = view.findViewById<ImageView>(R.id.profBut)
        profBut.setOnClickListener {
            if (popupMenu == null || !isSubMenuShowing) {
                showSubMenu(profBut)
            }
        }

        val fullCalendarBut : Button = view.findViewById(R.id.fullCalendarBut)
        fullCalendarBut.setOnClickListener {
            val i = Intent(view.context,FullCalendar::class.java)
            startActivity(i)
        }

        nextEvents = view.findViewById(R.id.nextEventsList)
        nextCults = view.findViewById(R.id.nextCultList)
        nextActs = view.findViewById(R.id.nextActList)
        nextEvents.layoutManager = LinearLayoutManager(view.context,LinearLayoutManager.HORIZONTAL, false)
        nextCults.layoutManager = LinearLayoutManager(view.context,LinearLayoutManager.HORIZONTAL, false)
        nextActs.layoutManager = LinearLayoutManager(view.context,LinearLayoutManager.HORIZONTAL, false)

        loadEvents(view.context)

        return view
    }

    private fun addWeekToCurrentDay(): RealmInstant {
        val today = LocalDateTime.now()
        val dateTime = today.withHour(0).withMinute(0).plusWeeks(1)

        return RealmInstant.from(dateTime.toEpochSecond(ZoneOffset.UTC),0)
    }

    private fun getCurrentWeekFirstDay(): RealmInstant {
        val today = LocalDateTime.now()
        val dateTime = today.withHour(0).withMinute(0).with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
        return RealmInstant.from(dateTime.toEpochSecond(ZoneOffset.UTC),0)
    }

    private fun loadEvents(context:Context) {
        val nextWeekDate = addWeekToCurrentDay()
        val currentWeekDate = getCurrentWeekFirstDay()

        val eventsFound = DatabaseConnector.db.query<Event>("date < $0",nextWeekDate).sort("date").find()
        val actsFound = DatabaseConnector.db.query<Activity>("date < $0",nextWeekDate).sort("date").find()
        val cultsFound = DatabaseConnector.db.query<Cult>("cancelDate < $0",currentWeekDate).sort("time").sort("weekDay").find()


        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mm a")
        val cultFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val weekdays = resources.getStringArray(R.array.createCultWeekdays)
        val events = eventsFound.map {
            val time = LocalDateTime.ofEpochSecond(it.date.epochSeconds,0, ZoneOffset.UTC)
            EagItemA(it._id,it.name,time.format(formatter))
        }
        val activities = actsFound.map {
            val time = LocalDateTime.ofEpochSecond(it.date.epochSeconds,0, ZoneOffset.UTC)
            EagItemA(it._id,it.name,time.format(formatter))
        }
        val cults = cultsFound.map {
            val time = LocalDateTime.ofEpochSecond(it.time.epochSeconds,0, ZoneOffset.UTC)
            EagItemA(it._id,it.name,weekdays[it.weekDay] + " " + time.format(cultFormatter))
        }

        val eventAdapter = EagNextListA(
            events,
            ResourcesCompat.getDrawable(context.resources,R.drawable.events_icon,null)!!
        )
        eventAdapter.onItemClick = onItemClick@{
            val i = Intent(requireContext(), CalendarDetail::class.java)
            val event = DatabaseConnector.db.query<Event>("_id == $0",it.id).find().firstOrNull()
            if(event == null) {
                Toast.makeText(requireContext(),getString(R.string.eventNotFound), Toast.LENGTH_SHORT).show()
                return@onItemClick
            }
            i.putExtra("name",event.name)
            i.putExtra("desc",event.desc)
            i.putExtra("day",LocalDateTime.ofEpochSecond(event.date.epochSeconds,
                0, ZoneOffset.UTC).format(dateFormatter))
            startActivity(i)
        }
        nextEvents.adapter = eventAdapter


        val actAdapter = EagNextListA(
            activities,
            ResourcesCompat.getDrawable(context.resources,R.drawable.actividades_icon,null)!!
        )
        actAdapter.onItemClick = onItemClick@{
            val i = Intent(requireContext(), CalendarDetail::class.java)
            val activity = DatabaseConnector.db.query<Activity>("_id == $0",it.id).find().firstOrNull()
            if(activity == null) {
                Toast.makeText(requireContext(),getString(R.string.eventNotFound), Toast.LENGTH_SHORT).show()
                return@onItemClick
            }
            i.putExtra("name",activity.name)
            i.putExtra("desc",activity.desc)
            i.putExtra("day",LocalDateTime.ofEpochSecond(activity.date.epochSeconds,
                0, ZoneOffset.UTC).format(dateFormatter))
            startActivity(i)
        }
        nextActs.adapter = actAdapter

        val cultAdapter = EagNextListA(
            cults,
            ResourcesCompat.getDrawable(context.resources,R.drawable.cults_icon,null)!!
        )
        cultAdapter.onItemClick = onItemClick@{
            val i = Intent(requireContext(), CalendarDetail::class.java)
            val cult = DatabaseConnector.db.query<Cult>("_id == $0",it.id).find().firstOrNull()
            if(cult == null) {
                Toast.makeText(requireContext(),getString(R.string.eventNotFound), Toast.LENGTH_SHORT).show()
                return@onItemClick
            }
            val time = LocalDateTime.ofEpochSecond(cult.time.epochSeconds,0, ZoneOffset.UTC)
            i.putExtra("name",cult.name)
            i.putExtra("desc",cult.desc)
            i.putExtra("day",weekdays[cult.weekDay] + " " + time.format(cultFormatter))
            startActivity(i)
        }
        nextCults.adapter = cultAdapter
    }

    private fun showSubMenu(view: View) {
        popupMenu = PopupMenu(requireContext(), view)
        popupMenu!!.menuInflater.inflate(R.menu.submenu_profile, popupMenu!!.menu)
        popupMenu!!.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(activity, MyProfile::class.java))
                    true
                }
                R.id.menu_emotion_registration -> {
                    if (DatabaseConnector.getIsAdmin()) {
                        startActivity(Intent(activity, SeeEmotions::class.java))
                    } else {
                        startActivity(Intent(activity, SendEmotion::class.java))
                    }
                    true
                }
                R.id.menu_suggestion_box -> {
                    if (DatabaseConnector.getIsAdmin()) {
                        startActivity(Intent(activity, SuggestionsMailbox::class.java))
                    } else {
                        startActivity(Intent(activity, SendSuggestion::class.java))
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu!!.setOnDismissListener {
            isSubMenuShowing = false
        }
        popupMenu!!.show()
        isSubMenuShowing = true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment mainpage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Mainpage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}