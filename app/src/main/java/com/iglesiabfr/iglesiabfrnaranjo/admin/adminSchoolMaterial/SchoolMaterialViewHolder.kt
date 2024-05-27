package com.iglesiabfr.iglesiabfrnaranjo.admin.adminSchoolMaterial

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemSchoolMaterialListBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.SchoolMaterial
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SchoolMaterialViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemSchoolMaterialListBinding.bind(view)
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

    fun render(
        schoolMaterialModel: SchoolMaterial,
        onClickListener: ((SchoolMaterial) -> Unit)?,
        onClickUpdate: (SchoolMaterial) -> Unit,
        onClickDelete: (Int) -> Unit
    ) {
        val initial = LocalDateTime.ofEpochSecond(schoolMaterialModel.initialMonth.epochSeconds, 0, ZoneOffset.UTC)
        val final = LocalDateTime.ofEpochSecond(schoolMaterialModel.finalMonth.epochSeconds, 0, ZoneOffset.UTC)
        binding.tvTeacher.text = schoolMaterialModel.teacherName
        binding.tvClase.text = schoolMaterialModel.clase
        binding.etInitialMonth.text = initial.format(formatter)
        binding.etFinalMonth.text = final.format(formatter)

        binding.btnUpdate.setOnClickListener {
            onClickUpdate(schoolMaterialModel)
        }

        binding.btnDelete.setOnClickListener {
            onClickDelete(absoluteAdapterPosition)
        }

        binding.root.setOnClickListener {
            onClickListener?.invoke(schoolMaterialModel)
        }
    }
}