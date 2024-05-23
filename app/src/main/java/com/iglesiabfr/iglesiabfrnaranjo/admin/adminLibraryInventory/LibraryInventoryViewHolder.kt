package com.iglesiabfr.iglesiabfrnaranjo.admin.adminLibraryInventory

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemInventaryLibraryListBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.LibraryInventory

class LibraryInventoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemInventaryLibraryListBinding.bind(view)

    fun render(
        libraryInventaryModel: LibraryInventory,
        onClickListener: (LibraryInventory) -> Unit,
        onClickDelete: (Int) -> Unit
    ) {
        binding.tvTitle.text = libraryInventaryModel.title
        binding.tvName.text = libraryInventaryModel.name
        binding.etPrice.text = libraryInventaryModel.price.toString()
        binding.etQuantity.text = libraryInventaryModel.quantity.toString()

        itemView.setOnClickListener { onClickListener(libraryInventaryModel) }
        binding.btnDelete.setOnClickListener { onClickDelete(absoluteAdapterPosition) }
    }
}