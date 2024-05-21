package com.iglesiabfr.iglesiabfrnaranjo.admin.adminSchoolMaterial

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemSchoolMaterialListBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.SchoolMaterial

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

        itemView.setOnClickListener { onClickListener(schoolMaterialModel) }
        binding.btnDelete.setOnClickListener { onClickDelete(absoluteAdapterPosition) }
    }
}