package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R

class SchoolMaterialAdapter(
    private val schoolMaterialList: List<SchoolMaterial>,
    private val onClickListener: (SchoolMaterial) -> Unit,
    private val onClickDelete:(Int) -> Unit
): RecyclerView.Adapter<SchoolMaterialViewHolder>() {

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

