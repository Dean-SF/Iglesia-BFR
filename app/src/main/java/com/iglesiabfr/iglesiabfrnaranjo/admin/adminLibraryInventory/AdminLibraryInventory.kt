package com.iglesiabfr.iglesiabfrnaranjo.admin.adminLibraryInventory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddInventoryAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityInventoryAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.schema.LibraryInventory
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminLibraryInventory : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var binding: ActivityAddInventoryAdminBinding
    private lateinit var binding1: ActivityInventoryAdminBinding
    private lateinit var adapter: LibraryInventoryAdapter
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInventoryAdminBinding.inflate(layoutInflater)
        binding1 = ActivityInventoryAdminBinding.inflate(layoutInflater)
        setContentView(binding1.root)

        realm = DatabaseConnector.db

        binding1.btnAddinventaryLibrary.setOnClickListener {
            // Set content view to binding1 after adding inventory library
            setContentView(binding.root)
        }

        binding.btnAddInventaryLibrary.setOnClickListener {
            createLibraryInventory()
        }

        binding.BackAddInventoryButton.setOnClickListener {
            setContentView(binding1.root)
            loadLibraryInventory() // Asegúrate de cargar los libros al volver
        }

        initRecyclerView()
        loadLibraryInventory() // Cargar los libros al inicio
    }

    private fun createLibraryInventory() {
        val title = binding.etTitle.text.toString()
        val name = binding.etName.text.toString()
        val quantityStr = binding.etQuantity.text.toString()
        val priceStr = binding.etPrice.text.toString()

        if (title.isNotEmpty() && name.isNotEmpty() && quantityStr.isNotEmpty() && priceStr.isNotEmpty()) {
            val quantity = quantityStr.toInt()
            val price = priceStr.toDouble()

            val libraryInventory = LibraryInventory().apply {
                this.title = title
                this.name = name
                this.quantity = quantity
                this.price = price
            }

            saveLibraryInventoryToDatabase(libraryInventory)

            // Limpiar los EditText después de agregar el libro
            binding.etTitle.text.clear()
            binding.etName.text.clear()
            binding.etQuantity.text.clear()
            binding.etPrice.text.clear()

        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLibraryInventoryToDatabase(libraryInventory: LibraryInventory) {// Obtenemos una referencia a la base de datos de Firebase
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    copyToRealm(libraryInventory)
                }
            }.onSuccess {
                loadLibraryInventory() // Recargar videos después de agregar
                Toast.makeText(this@AdminLibraryInventory, "Libro guardado correctamente", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@AdminLibraryInventory, "Error al guardar el Libro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteVideoFromDatabase(libraryInventory: LibraryInventory) {
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    findLatest(libraryInventory).also {
                        delete(it!!)
                    }
                }
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    loadLibraryInventory() // Recargar videos después de eliminar
                    Toast.makeText(this@AdminLibraryInventory, "Libro eliminado correctamente", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminLibraryInventory, "Error al eliminar el libro", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadLibraryInventory() {
        lifecycleScope.launch(Dispatchers.IO) {
            val libraryInventorys = realm.query<LibraryInventory>().find()
            withContext(Dispatchers.Main) {
                adapter.submitList(libraryInventorys)
            }
        }
    }

    private fun initRecyclerView(){
        adapter = LibraryInventoryAdapter(
            onClickListener = { libraryInventory: LibraryInventory -> onItemSelected(libraryInventory) },
            onClickDelete = { position: Int -> onDeletedItem(position) }
        )
        binding1.recyclerinventaryLibrary.layoutManager = llmanager
        binding1.recyclerinventaryLibrary.adapter = adapter
    }

    private fun onItemSelected(libraryInventory: LibraryInventory) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(libraryInventory.name.toString()))
        startActivity(intent)
    }

    private fun onDeletedItem(position: Int) {
        val libraryInventory = adapter.currentList[position]
        deleteVideoFromDatabase(libraryInventory)
        adapter.notifyItemRemoved(position)
    }
}
