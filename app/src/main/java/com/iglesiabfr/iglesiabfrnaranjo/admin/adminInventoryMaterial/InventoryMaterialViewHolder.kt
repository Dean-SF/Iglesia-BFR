package com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemInventoryMaterialListBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.InventoryMaterial

class InventoryMaterialViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemInventoryMaterialListBinding.bind(view)

    fun render(
        inventoryMaterialModel: InventoryMaterial,
        onClickListener: (InventoryMaterial) -> Unit,
        onClickDelete: (Int) -> Unit
    ) {
        binding.tvName.text = inventoryMaterialModel.name
        binding.tvQuantity.text = inventoryMaterialModel.quantity.toString()
        binding.etType.text = inventoryMaterialModel.type

        itemView.setOnClickListener { onClickListener(inventoryMaterialModel) }
        binding.btnDelete.setOnClickListener { onClickDelete(absoluteAdapterPosition) }
    }
}

