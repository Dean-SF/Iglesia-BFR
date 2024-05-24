package com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddInventoryMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityInventoryMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.schema.InventoryMaterial
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class AdminInventoryMaterial : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var binding: ActivityAddInventoryMaterialAdminBinding
    private lateinit var binding1: ActivityInventoryMaterialAdminBinding
    private lateinit var adapter: InventoryMaterialAdapter
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInventoryMaterialAdminBinding.inflate(layoutInflater)
        binding1 = ActivityInventoryMaterialAdminBinding.inflate(layoutInflater)
        setContentView(binding1.root)

        realm = DatabaseConnector.db

        binding1.btnAddInventoryMaterial.setOnClickListener {
            // Cambiar a la vista que muestra los productos agregados
            setContentView(binding.root)
        }

        binding1.BackInventoryMaterialButton.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        binding.btnSaveInventoryMaterial.setOnClickListener {
            createInventoryMaterial()
        }

        binding.BackAddInventoryAdminButton.setOnClickListener {
            setContentView(binding1.root)
            loadInventoryMaterial() // Asegúrate de cargar los materiales al volver
        }

        initRecyclerView()
        loadInventoryMaterial() // Cargar los materiales al inicio
    }

    private fun createInventoryMaterial() {
        val name = binding.etName.text.toString()
        val quantityStr = binding.etQuantity.text.toString()
        val type = binding.etPrice.text.toString()

        if (name.isNotEmpty() && quantityStr.isNotEmpty() && type.isNotEmpty()) {
            val quantity = quantityStr.toInt()

            val inventoryMaterial = InventoryMaterial().apply {
                this.name = name
                this.quantity = quantity
                this.type = type
            }

            saveInventoryMaterialToDatabase(inventoryMaterial)

            // Limpiar los EditText después de agregar los materiales
            binding.etName.text.clear()
            binding.etQuantity.text.clear()
            binding.etPrice.text.clear()

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
            }.onFailure {
                Toast.makeText(this@AdminInventoryMaterial, "Error al guardar el material", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@AdminInventoryMaterial, "Material eliminado correctamente", Toast.LENGTH_SHORT).show()
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

    private fun initRecyclerView(){
        adapter = InventoryMaterialAdapter(
            onClickListener = { inventoryMaterial: InventoryMaterial -> onItemSelected(inventoryMaterial) },
            onClickDelete = { position: Int -> onDeletedItem(position) }
        )
        binding1.recyclerInventoryMaterial.layoutManager = llmanager
        binding1.recyclerInventoryMaterial.adapter = adapter
    }

    private fun onItemSelected(inventoryMaterial: InventoryMaterial) {
        Toast.makeText(this, inventoryMaterial.name, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position: Int) {
        val inventoryMaterial = adapter.currentList[position]
        deleteInventoryMaterialFromDatabase(inventoryMaterial)
        adapter.notifyItemRemoved(position)
    }
}

