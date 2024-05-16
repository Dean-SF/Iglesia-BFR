package com.iglesiabfr.iglesiabfrnaranjo.admin.adminLibraryInventory

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddInventoryAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityInventoryAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage

class AdminLibraryInventory : AppCompatActivity() {

    private lateinit var binding: ActivityAddInventoryAdminBinding
    private lateinit var binding1: ActivityInventoryAdminBinding
    private lateinit var adapter: BookAdapter
    private val llmanager = LinearLayoutManager(this)
    private val bookMutableList = mutableListOf<Book>() // Lista mutable de librería

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInventoryAdminBinding.inflate(layoutInflater)
        binding1 = ActivityInventoryAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding1.btnAddBook.setOnClickListener {
            // Set content view to binding1 after adding inventory library
            setContentView(binding.root)
        }

        binding1.BackAdminEventCultButton.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        binding.btnAddBook.setOnClickListener {
            createBook()
        }
        initRecyclerView()

        binding.BackAdminEventCultButton.setOnClickListener {
            setContentView(binding1.root)
        }
    }

    private fun createBook() {
        val title = binding.etTitle.text.toString()
        val name = binding.etName.text.toString()
        val quantityStr = binding.etQuantity.text.toString()
        val priceStr = binding.etPrice.text.toString()

        if (title.isNotEmpty() && name.isNotEmpty() && quantityStr.isNotEmpty() && priceStr.isNotEmpty()) {
            val quantity = quantityStr.toInt()
            val price = priceStr.toDouble()

            val book = Book(
                title = title,
                name = name,
                quantity = quantity,
                price = price
            )

            bookMutableList.add(book) // Agregar el nuevo libro a la lista
            adapter.submitList(bookMutableList) // Actualizar el RecyclerView con la nueva lista

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
            onClickListener = { book -> onItemSelected(book) },
            onClickDelete = { position -> onDeletedItem(position) }
        )
        binding1.recyclerBook.layoutManager = llmanager
        binding1.recyclerBook.adapter = adapter
    }

    private fun onItemSelected(book: Book) {
        Toast.makeText(this, book.name, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position: Int) {
        if (position in 0 until bookMutableList.size) {
            bookMutableList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }
}
