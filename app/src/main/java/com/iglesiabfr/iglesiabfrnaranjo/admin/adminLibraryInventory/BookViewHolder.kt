package com.iglesiabfr.iglesiabfrnaranjo.admin.adminLibraryInventory

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ItemBookListBinding

class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemBookListBinding.bind(view)

    fun render(
        bookModel: Book,
        onClickListener: (Book) -> Unit,
        onClickDelete: (Int) -> Unit
    ) {
        binding.tvTitle.text = bookModel.title
        binding.tvName.text = bookModel.name
        binding.etPrice.text = bookModel.price.toString()
        binding.etQuantity.text = bookModel.quantity.toString()

        itemView.setOnClickListener { onClickListener(bookModel) }
        binding.btnDelete.setOnClickListener { onClickDelete(absoluteAdapterPosition) }
    }
}