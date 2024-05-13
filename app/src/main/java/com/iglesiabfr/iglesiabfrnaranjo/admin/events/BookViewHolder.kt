package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.view.View
import android.widget.Toast
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
        binding.etPrice.id = bookModel.price
        binding.etQuantity.id = bookModel.quantity
        itemView.setOnClickListener { Toast.makeText(itemView.context, "KotlinMan", Toast.LENGTH_SHORT).show() }
        binding.btnDelete.setOnClickListener { onClickDelete(absoluteAdapterPosition) }
    }
}