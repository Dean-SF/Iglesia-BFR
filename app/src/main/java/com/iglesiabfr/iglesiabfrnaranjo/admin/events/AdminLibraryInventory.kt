package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R

class AdminLibraryInventory : AppCompatActivity() {
    lateinit var mRecyclerView : RecyclerView
    val mAdapter : RecyclerAdapter = RecyclerAdapter()
    lateinit var mBooks: MutableList<Book>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_admin)
        setUpRecyclerView()
        mBooks = getBooks() // Obtener los libros existentes
    }

    private fun setUpRecyclerView(){
        mRecyclerView = findViewById(R.id.rvLibraryList) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.RecyclerAdapter(getBooks(), this)
        mRecyclerView.adapter = mAdapter
    }

    private fun addBook(title: String, name: String, quantity: Int, price: Int, imgUrl: String) {
        mBooks.add(Book(title, name, quantity, price, imgUrl))
        mAdapter.notifyDataSetChanged()
        // Aquí puedes guardar la lista actualizada en tu base de datos o en SharedPreferences
    }

    private fun updateBook(position: Int, title: String, name: String, quantity: Int, price: Int, imgUrl: String) {
        mBooks[position].title = title
        mBooks[position].name = name
        mBooks[position].quantity = quantity
        mBooks[position].price = price
        mBooks[position].imgUrl = imgUrl
        mAdapter.notifyDataSetChanged()
        // Aquí puedes guardar la lista actualizada en tu base de datos o en SharedPreferences
    }

    private fun deleteBook(position: Int) {
        mBooks.removeAt(position)
        mAdapter.notifyDataSetChanged()
        // Aquí puedes guardar la lista actualizada en tu base de datos o en SharedPreferences
    }

    private fun getBooks(): MutableList<Book> {
        // Aquí deberías obtener los libros de tu base de datos o de SharedPreferences
        // Por ahora, devolveremos una lista vacía
        return mutableListOf()
    }

    /*fun getBooks(): MutableList<Book>{
        var books:MutableList<Book> = ArrayList()
        books.add(Book("Spiderman", "Marvel", 4, 12, "gf"))
        books.add(Book("Daredevil", "Marvel", 3, 43, "gf"))
        books.add(Book("Wolverine", "Marvel", 5, 55, "gf"))
        books.add(Book("Batman", "DC", 1, 12, "gf"))
        books.add(Book("Thor", "Marvel", 54, 12, "gf"))
        books.add(Book("Flash", "DC", 34, 12, "gf"))
        books.add(Book("Green Lantern", "DC", 55, 12, "gf"))
        books.add(Book("Wonder Woman", "DC", 4, 12, "gf"))
        return books
    }*/
}