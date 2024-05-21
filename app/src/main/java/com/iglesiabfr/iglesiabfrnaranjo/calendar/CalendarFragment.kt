package com.iglesiabfr.iglesiabfrnaranjo.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.calendar.utils.CalendarEntry
import com.iglesiabfr.iglesiabfrnaranjo.calendar.utils.dateFormatter
import com.iglesiabfr.iglesiabfrnaranjo.databinding.CalendarDayBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.CalendarHeaderBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.CalendarEventItemViewBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.CalendarFragmentBinding
import com.iglesiabfr.iglesiabfrnaranjo.calendar.utils.displayText
import com.iglesiabfr.iglesiabfrnaranjo.calendar.utils.getCalendarData
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.LinkedList

class CalendarEntryAdapter(val dataList: LinkedList<CalendarEntry>,val types : Array<String>) : RecyclerView.Adapter<CalendarEntryAdapter.CalendarEntryViewHolder>() {

    var onItemClick : ((CalendarEntry) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEntryViewHolder {
        return CalendarEntryViewHolder(
            CalendarEventItemViewBinding.inflate(parent.context.layoutInflater, parent, false),
            types
        )
    }

    override fun onBindViewHolder(viewHolder: CalendarEntryViewHolder, position: Int) {
        val currentItem = dataList[position]
        viewHolder.bind(currentItem)
        viewHolder.itemView.setOnClickListener {
            Log.d("sisi","funque")
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class CalendarEntryViewHolder(private val binding: CalendarEventItemViewBinding, val types : Array<String>) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: CalendarEntry) {
            when(entry) {
                is CalendarEntry.EventCalendar -> {
                    binding.entryName.text = entry.name
                    binding.entryDate.text = entry.date.format(dateFormatter)
                    binding.entryType.text = types[entry.type.ordinal]
                }
                is CalendarEntry.CultCalendar -> {
                    binding.entryName.text = entry.name
                    binding.entryDate.text = entry.time
                    binding.entryType.text = types[entry.type.ordinal]
                }
            }
        }
    }
}

class CalendarFragment() : Fragment(R.layout.calendar_fragment) {

    private var selectedDate: LocalDate? = null

    private lateinit var entriesAdapter : CalendarEntryAdapter

    private lateinit var calendarEntries : Map<LocalDate , List<CalendarEntry>>

    private lateinit var binding: CalendarFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        entriesAdapter = CalendarEntryAdapter(
            LinkedList<CalendarEntry>(),
            requireContext().resources.getStringArray(R.array.calendarType)
        )

        entriesAdapter.onItemClick = {
            val i = Intent(requireContext(),CalendarDetail::class.java)
            when(it) {
                is CalendarEntry.EventCalendar -> {
                    i.putExtra("name",it.name)
                    i.putExtra("desc",it.desc)
                    i.putExtra("day",it.date.format(dateFormatter))
                }

                is CalendarEntry.CultCalendar -> {
                    i.putExtra("name",it.name)
                    i.putExtra("desc",it.desc)
                    i.putExtra("day",it.time)
                }
            }
            startActivity(i)
        }

        calendarEntries = getCalendarData(requireContext()).groupBy {
            when(it) {
                is CalendarEntry.EventCalendar -> {
                    it.date.toLocalDate()
                }
                is CalendarEntry.CultCalendar -> {
                    it.date.toLocalDate()
                }
            }
        }

        binding = CalendarFragmentBinding.bind(view)

        binding.exFiveRv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = entriesAdapter
        }

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)
        configureBinders(daysOfWeek)
        binding.exFiveCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.exFiveCalendar.scrollToMonth(currentMonth)

        binding.exFiveCalendar.monthScrollListener = { month ->
            binding.exFiveMonthYearText.text = month.yearMonth.displayText()
            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.exFiveCalendar.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        binding.exFiveNextMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
    }

    private fun updateAdapterForDate(date: LocalDate?) {
        entriesAdapter.dataList.clear()
        entriesAdapter.dataList.addAll(calendarEntries[date].orEmpty().sortedBy {
            when(it) {
                is CalendarEntry.EventCalendar -> {
                    it.date.toLocalTime()
                }
                is CalendarEntry.CultCalendar -> {
                    it.date.toLocalTime()
                }
            }
        })
        entriesAdapter.notifyDataSetChanged()
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            val binding = this@CalendarFragment.binding
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }
                            updateAdapterForDate(day.date)
                        }
                    }
                }
            }
        }
        binding.exFiveCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val context = container.binding.root.context
                val textView = container.binding.dayNumText
                val layout = container.binding.exFiveDayLayout
                textView.text = data.date.dayOfMonth.toString()

                val isThereSomethingView = container.binding.isThereSomething
                isThereSomethingView.background = null

                if (data.position == DayPosition.MonthDate) {
                    textView.setTextColorRes(R.color.blue)
                    layout.setBackgroundResource(if (selectedDate == data.date) R.drawable.calendar_selected_bg else 0)

                    val currentEntries = calendarEntries[data.date]
                    if (currentEntries != null) {
                        isThereSomethingView.setBackgroundColor(context.getColorCompat(R.color.blue))
                    }
                } else {
                    textView.setTextColorRes(R.color.light_blue)
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        val typeFace = ResourcesCompat.getFont(requireView().context,R.font.comfortaa_light)
        binding.exFiveCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].displayText(uppercase = true)
                                tv.setTextColorRes(R.color.blue)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                                tv.typeface = typeFace
                            }
                    }
                }
            }
    }
}
