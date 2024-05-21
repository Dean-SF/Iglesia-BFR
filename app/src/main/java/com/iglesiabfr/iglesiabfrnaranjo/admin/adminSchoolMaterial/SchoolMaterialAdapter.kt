package com.iglesiabfr.iglesiabfrnaranjo.admin.adminSchoolMaterial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.SchoolMaterial

class SchoolMaterialAdapter(
    private val onClickListener: (SchoolMaterial) -> Unit,
    private val onClickDelete:(Int) -> Unit
): RecyclerView.Adapter<SchoolMaterialViewHolder>() {

    private val schoolMaterialList = mutableListOf<SchoolMaterial>()

    val currentList: List<SchoolMaterial>
        get() = schoolMaterialList

    fun submitList(newList: List<SchoolMaterial>) {
        schoolMaterialList.clear()
        schoolMaterialList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolMaterialViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SchoolMaterialViewHolder(layoutInflater.inflate(R.layout.item_school_material_list, parent, false))
    }

    override fun onBindViewHolder(holder: SchoolMaterialViewHolder, position: Int) {
        val item = schoolMaterialList[position]
        holder.render(item, onClickListener, onClickDelete)
    }

    override fun getItemCount(): Int = schoolMaterialList.size
}

