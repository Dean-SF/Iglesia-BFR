package com.iglesiabfr.iglesiabfrnaranjo.admin.cults

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial.EventCultViewHolder
import com.iglesiabfr.iglesiabfrnaranjo.schema.AttendanceCults

class EventCultAdapter (
    private val onClickListener: ((AttendanceCults) -> Unit)?,
    private val onClickDelete: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<EventCultViewHolder>() {

    val attendanceCultList = mutableListOf<AttendanceCults>()

    val currentList: List<AttendanceCults>
        get() = attendanceCultList

    fun submitList(newList: List<AttendanceCults>) {
        attendanceCultList.clear()
        attendanceCultList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventCultViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_event_list, parent, false)
        return EventCultViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventCultViewHolder, position: Int) {
        val item = attendanceCultList[position]
        if (onClickDelete != null) {
            holder.render(item, onClickListener, onClickDelete)
        }
    }

    override fun getItemCount(): Int = attendanceCultList.size
}

