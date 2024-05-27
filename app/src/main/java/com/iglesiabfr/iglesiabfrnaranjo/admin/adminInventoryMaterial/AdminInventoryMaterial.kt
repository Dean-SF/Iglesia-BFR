package com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddInventoryMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityInventoryMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.schema.InventoryMaterial
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminInventoryMaterial : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var binding: ActivityAddInventoryMaterialAdminBinding
    private lateinit var binding1: ActivityInventoryMaterialAdminBinding
    private lateinit var adapter: InventoryMaterialAdapter
    private val llmanager = LinearLayoutManager(this)
    private var currentBinding = 0
    private lateinit var currentInventoryMaterial: InventoryMaterial
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInventoryMaterialAdminBinding.inflate(layoutInflater)
        binding1 = ActivityInventoryMaterialAdminBinding.inflate(layoutInflater)
        setContentView(binding1.root)
        currentBinding = 0

        realm = DatabaseConnector.db

        binding1.btnAddInventoryMaterial.setOnClickListener {
            // Cambiar a la vista que muestra los productos agregados
            setContentView(binding.root)
            currentBinding = 1
        }

        binding.btnSaveInventoryMaterial.setOnClickListener {
            createOrUpdateInventoryMaterial()
        }

        initRecyclerView()
        loadInventoryMaterial() // Cargar los materiales al inicio
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        if(currentBinding == 1) {
            currentBinding = 0
            setContentView(binding1.root)
            loadInventoryMaterial()
            return
        }
        super.onBackPressed()
    }

    private fun createOrUpdateInventoryMaterial() {
        val name = binding.etName.text.toString()
        val quantityStr = binding.etQuantity.text.toString()
        val type = binding.etPrice.text.toString()

        if (name.isNotEmpty() && quantityStr.isNotEmpty() && type.isNotEmpty()) {
            val quantity = quantityStr.toInt()

            if (isUpdating) {
                // Actualizar material existente
                updateInventoryMaterialFromDatabase(currentInventoryMaterial, name, quantity, type)
            } else {
                // Crear nuevo material
                val newInventoryMaterial = InventoryMaterial().apply {
                    this.name = name
                    this.quantity = quantity
                    this.type = type
                }
                saveInventoryMaterialToDatabase(newInventoryMaterial)
            }

            // Limpiar el formulario
            binding.etName.text.clear()
            binding.etQuantity.text.clear()
            binding.etPrice.text.clear()

            // Cambiar a la vista principal
            setContentView(binding1.root)
            currentBinding = 0
            binding.btnSaveInventoryMaterial.setText("Guardar") // Reset the button text
            isUpdating = false // Resetear el estado de actualización
        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInventoryMaterialToDatabase(inventoryMaterial: InventoryMaterial) {
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    copyToRealm(inventoryMaterial)
                }
            }.onSuccess {
                loadInventoryMaterial() // Recargar materiales después de agregar
                Toast.makeText(this@AdminInventoryMaterial, "Material guardado correctamente", Toast.LENGTH_SHORT).show()
                setContentView(binding1.root) // Cambiar a la vista principal
                currentBinding = 0
            }.onFailure {
                Toast.makeText(this@AdminInventoryMaterial, "Error al guardar el material", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateInventoryMaterialFromDatabase(inventoryMaterial: InventoryMaterial, name: String, quantity: Int, type: String) {
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    findLatest(inventoryMaterial)?.apply {
                        this.name = name
                        this.quantity = quantity
                        this.type = type
                    }
                }
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    loadInventoryMaterial() // Recargar materiales después de actualizar
                    Toast.makeText(this@AdminInventoryMaterial, "Material actualizado correctamente", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminInventoryMaterial, "Error al actualizar el material", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteInventoryMaterialFromDatabase(inventoryMaterial: InventoryMaterial) {
        lifecycleScope.launch {
            runCatching {
                realm.write {
                    findLatest(inventoryMaterial).also {
                        delete(it!!)
                    }
                }
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    loadInventoryMaterial() // Recargar videos después de eliminar
                    Toast.makeText(
                        this@AdminInventoryMaterial,
                        "Material eliminado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AdminInventoryMaterial, "Error al eliminar el material", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadInventoryMaterial() {
        lifecycleScope.launch(Dispatchers.IO) {
            val inventoryMaterials = realm.query<InventoryMaterial>().find()
            withContext(Dispatchers.Main) {
                adapter.submitList(inventoryMaterials)
            }
        }
    }

    private fun onItemSelected(inventoryMaterial: InventoryMaterial) {
        currentInventoryMaterial = inventoryMaterial
        binding.etName.setText(inventoryMaterial.name)
        binding.etQuantity.setText(inventoryMaterial.quantity.toString())
        binding.etPrice.setText(inventoryMaterial.type)

        setContentView(binding.root)
        currentBinding = 1
    }

    private fun initRecyclerView(){
        adapter = InventoryMaterialAdapter(
            onClickListener = null,
            /*onClickListener = { inventoryMaterial: InventoryMaterial ->
                onItemSelected(inventoryMaterial) },*/
            onClickUpdate = { inventoryMaterial ->
                // Switch to the update view and populate fields
                setContentView(binding.root)
                currentBinding = 1
                binding.etName.setText(inventoryMaterial.name)
                binding.etQuantity.setText(inventoryMaterial.quantity.toString())
                binding.etPrice.setText(inventoryMaterial.type)
                binding.btnSaveInventoryMaterial.setText("Actualizar")
                isUpdating = true
                currentInventoryMaterial = inventoryMaterial
            },
            onClickDelete = { position: Int -> onDeletedItem(position) }
        )
        binding1.recyclerInventoryMaterial.layoutManager = llmanager
        binding1.recyclerInventoryMaterial.adapter = adapter
    }

    private fun onDeletedItem(position: Int) {
        val inventoryMaterial = adapter.currentList[position]
        deleteInventoryMaterialFromDatabase(inventoryMaterial)
        adapter.notifyItemRemoved(position)
    }
}

