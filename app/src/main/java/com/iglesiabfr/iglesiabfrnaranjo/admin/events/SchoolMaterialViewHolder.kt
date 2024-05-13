package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemSchoolMaterialListBinding

class SchoolMaterialViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSchoolMaterialListBinding.bind(view)

    fun render(
        schoolMaterialModel: SchoolMaterial,
        onClickListener: (SchoolMaterial) -> Unit,
        onClickDelete: (Int) -> Unit
    ) {
        binding.tvTeacher.text = schoolMaterialModel.teacherName
        binding.tvClase.text = schoolMaterialModel.clase
        binding.etInitialMonth.text = schoolMaterialModel.initialMonth.toString()
        binding.etFinalMonth.text = schoolMaterialModel.finalMonth.toString()
        itemView.setOnClickListener { Toast.makeText(itemView.context, "KotlinMan", Toast.LENGTH_SHORT).show() }
        binding.btnDelete.setOnClickListener { onClickDelete(absoluteAdapterPosition) }
    }
}