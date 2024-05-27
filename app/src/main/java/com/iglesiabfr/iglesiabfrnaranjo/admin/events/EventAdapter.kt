package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial.EventViewHolder
import com.iglesiabfr.iglesiabfrnaranjo.schema.Attendance
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video

class EventAdapter (
    private val onClickListener: ((Attendance) -> Unit)?,
    private val onClickDelete: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<EventViewHolder>() {

    val videoList = mutableListOf<Attendance>()

    val currentList: List<Attendance>
        get() = videoList

    fun submitList(newList: List<Attendance>) {
        videoList.clear()
        videoList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_event_list, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = videoList[position]
        if (onClickDelete != null) {
            holder.render(item, onClickListener, onClickDelete)
        }
    }

    override fun getItemCount(): Int = videoList.size
}

