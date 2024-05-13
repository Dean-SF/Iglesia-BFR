package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityInventoryAdminBinding

class AdminLibraryInventory : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryAdminBinding
    private var bookMutableList:MutableList<Book> =
        BookProvider.bookList.toMutableList()
    private lateinit var adapter: BookAdapter
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddBook.setOnClickListener { createBook() }
        initRecyclerView()
    }

    private fun createBook() {
        val title = binding.etTitle.text.toString()
        val name = binding.etName.text.toString()
        val quantityStr = binding.etQuantity.text.toString()
        val priceStr = binding.etPrice.text.toString()

        if (title.isNotEmpty() && name.isNotEmpty() && quantityStr.isNotEmpty() && priceStr.isNotEmpty()) {
            val quantity = quantityStr.toInt()
            val price = priceStr.toInt()

            val book = Book(
                title = title,
                name = name,
                quantity = quantity,
                price = price
            )

            bookMutableList.add(index = 1, book)
            adapter.notifyItemInserted(1)
            llmanager.scrollToPositionWithOffset(1, 10)

            // Limpiar los EditText después de agregar el libro
            binding.etTitle.text.clear()
            binding.etName.text.clear()
            binding.etQuantity.text.clear()
            binding.etPrice.text.clear()
        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView(){
        adapter = BookAdapter(
            bookList = bookMutableList,
            onClickListener = { book -> onItemSelected(book) },
            onClickDelete = { position -> onDeletedItem(position) }
        )
        binding.recyclerBook.layoutManager = llmanager
        binding.recyclerBook.adapter = adapter
    }

    private fun onItemSelected(book: Book) {
        Toast.makeText(this, book.name, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position: Int) {
        bookMutableList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

}