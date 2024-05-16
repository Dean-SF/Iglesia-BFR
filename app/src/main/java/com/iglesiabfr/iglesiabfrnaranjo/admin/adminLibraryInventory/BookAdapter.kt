package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R

class BookAdapter(
    private val onClickListener: (Book) -> Unit,
    private val onClickDelete:(Int) -> Unit
): RecyclerView.Adapter<BookViewHolder>() {

    private val bookList = mutableListOf<Book>()

    fun submitList(newList: List<Book>) {
        bookList.clear()
        bookList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BookViewHolder(layoutInflater.inflate(R.layout.item_book_list, parent, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val item = bookList[position]
        holder.render(item, onClickListener, onClickDelete)
    }

    override fun getItemCount(): Int = bookList.size
}

