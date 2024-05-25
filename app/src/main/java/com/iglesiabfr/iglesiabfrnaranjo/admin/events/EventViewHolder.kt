package com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemEventListBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.Attendance

class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemEventListBinding.bind(view)

    fun render(
        eventModel: Attendance,
        onClickListener: (Attendance) -> Unit,
        onClickDelete: (Int) -> Unit
    ) {
        binding.tvName.text = eventModel.namePerson

        itemView.setOnClickListener { onClickListener(eventModel) }
        binding.btnDelete.setOnClickListener { onClickDelete(absoluteAdapterPosition) }
    }
}

