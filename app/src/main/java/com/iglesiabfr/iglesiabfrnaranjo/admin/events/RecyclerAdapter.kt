package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.squareup.picasso.Picasso

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var books: MutableList<Book> = ArrayList()
    private lateinit var context: Context

    fun RecyclerAdapter(books : MutableList<Book>, context: Context){
        this.books = books
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = books.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_book_list, parent, false))
    }

    override fun getItemCount(): Int {
        return books.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookName = view.findViewById(R.id.tvTitle) as TextView
        val name = view.findViewById(R.id.tvName) as TextView
        val price = view.findViewById(R.id.etPrice) as EditText
        val quantity = view.findViewById(R.id.etQuantity) as EditText
        val avatar = view.findViewById(R.id.ivAvatar) as ImageView

        fun bind(book:Book, context: Context){
            bookName.text = book.title
            name.text = book.name
            quantity.id = book.quantity
            price.id = book.price
            itemView.setOnClickListener(View.OnClickListener {
                Toast.makeText(context, book.title, Toast.LENGTH_SHORT).show()
            })
            avatar.loadUrl(book.imgUrl)
        }
        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}

