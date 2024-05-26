package com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.InventoryMaterial

class InventoryMaterialAdapter(
    private val onClickListener: ((InventoryMaterial) -> Unit)?,
    private val onClickDelete:(Int) -> Unit
): RecyclerView.Adapter<InventoryMaterialViewHolder>() {

    private val inventoryMaterialList = mutableListOf<InventoryMaterial>()

    val currentList: List<InventoryMaterial>
        get() = inventoryMaterialList

    fun submitList(newList: List<InventoryMaterial>) {
        inventoryMaterialList.clear()
        inventoryMaterialList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryMaterialViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return InventoryMaterialViewHolder(layoutInflater.inflate(R.layout.item_inventory_material_list, parent, false))
    }

    override fun onBindViewHolder(holder: InventoryMaterialViewHolder, position: Int) {
        val item = inventoryMaterialList[position]
        holder.render(item, onClickListener, onClickDelete)
    }

    override fun getItemCount(): Int = inventoryMaterialList.size
}



