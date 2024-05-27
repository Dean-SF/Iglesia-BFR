package com.iglesiabfr.iglesiabfrnaranjo.admin.adminLibraryInventory

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddInventoryAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityInventoryAdminBinding
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
    private var currentBinding = 0
    private lateinit var currentLibraryInventory: LibraryInventory
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInventoryAdminBinding.inflate(layoutInflater)
        binding1 = ActivityInventoryAdminBinding.inflate(layoutInflater)
        setContentView(binding1.root)
        currentBinding = 0

        realm = DatabaseConnector.db

        binding1.btnAddinventaryLibrary.setOnClickListener {
            // Set content view to binding1 after adding inventory library
            setContentView(binding.root)
            currentBinding = 1
        }

        binding.btnAddInventaryLibrary.setOnClickListener {
            createOrUpdateLibraryInventory()
        }

        initRecyclerView()
        loadLibraryInventory() // Cargar los libros al inicio
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        if(currentBinding == 1) {
            currentBinding = 0
            setContentView(binding1.root)
            loadLibraryInventory()
            return
        }
        super.onBackPressed()
    }

    private fun createOrUpdateLibraryInventory() {
        val title = binding.etTitle.text.toString()
        val name = binding.etName.text.toString()
        val quantityStr = binding.etQuantity.text.toString()
        val priceStr = binding.etPrice.text.toString()

        if (title.isNotEmpty() && name.isNotEmpty() && quantityStr.isNotEmpty() && priceStr.isNotEmpty()) {
            val quantity = quantityStr.toInt()
            val price = priceStr.toDouble()

            if (isUpdating) {
                // Actualizar material existente
                updateLibraryInventoryFromDatabase(currentLibraryInventory, title, name, quantity, price)
            } else {
                val newLibraryInventory = LibraryInventory().apply {
                    this.title = title
                    this.name = name
                    this.quantity = quantity
                    this.price = price
                }

                saveLibraryInventoryToDatabase(newLibraryInventory)
            }

            // Limpiar los EditText después de agregar el libro
            binding.etTitle.text.clear()
            binding.etName.text.clear()
            binding.etQuantity.text.clear()
            binding.etPrice.text.clear()

            // Cambiar a la vista principal
            setContentView(binding1.root)
            currentBinding = 0
            binding.btnAddInventaryLibrary.setText("Guardar") // Reset the button text
            isUpdating = false // Resetear el estado de actualización
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
                setContentView(binding1.root) // Cambiar a la vista principal
                currentBinding = 0
            }.onFailure {
                Toast.makeText(this@AdminLibraryInventory, "Error al guardar el Libro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLibraryInventoryFromDatabase(libraryInventory: LibraryInventory, title: String, name: String, quantity: Int, price: Double) {
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    findLatest(libraryInventory)?.apply {
                        this.title = title
                        this.name = name
                        this.quantity = quantity
                        this.price = price
                    }
                }
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    loadLibraryInventory() // Recargar materiales después de actualizar
                    Toast.makeText(this@AdminLibraryInventory, "Libro actualizado correctamente", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminLibraryInventory, "Error al actualizar el libro", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteLibraryInventoryFromDatabase(libraryInventory: LibraryInventory) {
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

    private fun onItemSelected(libraryInventory: LibraryInventory) {
        currentLibraryInventory = libraryInventory
        binding.etTitle.setText(libraryInventory.title)
        binding.etName.setText(libraryInventory.title)
        binding.etQuantity.setText(libraryInventory.quantity.toString())
        binding.etPrice.setText(libraryInventory.price.toString())

        setContentView(binding.root)
        currentBinding = 1
    }

    private fun initRecyclerView(){
        adapter = LibraryInventoryAdapter(
            onClickListener = null,
            /*onClickListener = { libraryInventory: LibraryInventory ->
                onItemSelected(libraryInventory) },*/
            onClickUpdate = { libraryInventory ->
                // Switch to the update view and populate fields
                setContentView(binding.root)
                currentBinding = 1
                binding.etTitle.setText(libraryInventory.title)
                binding.etName.setText(libraryInventory.title)
                binding.etQuantity.setText(libraryInventory.quantity.toString())
                binding.etPrice.setText(libraryInventory.price.toString())
                binding.btnAddInventaryLibrary.setText("Actualizar")
                isUpdating = true
                currentLibraryInventory = libraryInventory
            },
            onClickDelete = { position: Int -> onDeletedItem(position) }
        )
        binding1.recyclerinventaryLibrary.layoutManager = llmanager
        binding1.recyclerinventaryLibrary.adapter = adapter
    }


    private fun onDeletedItem(position: Int) {
        val libraryInventory = adapter.currentList[position]
        deleteLibraryInventoryFromDatabase(libraryInventory)
        adapter.notifyItemRemoved(position)
    }
}
