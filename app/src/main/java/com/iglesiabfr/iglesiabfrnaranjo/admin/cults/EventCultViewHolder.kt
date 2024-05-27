package com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemEventListBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.AttendanceCults

class EventCultViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemEventListBinding.bind(view)

    fun render(
        eventCultModel: AttendanceCults,
        onClickListener: ((AttendanceCults) -> Unit)?,
        onClickDelete: (Int) -> Unit
    ) {
        binding.tvName.text = eventCultModel.namePerson

        itemView.setOnClickListener { onClickListener?.invoke(eventCultModel) }
        binding.btnDelete.setOnClickListener { onClickDelete(absoluteAdapterPosition) }
    }
}

