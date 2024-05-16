package com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddInventoryMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityInventoryMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage

class AdminInventoryMaterial : AppCompatActivity() {

    private lateinit var binding: ActivityAddInventoryMaterialAdminBinding
    private lateinit var binding1: ActivityInventoryMaterialAdminBinding
    private lateinit var adapter: InventoryMaterialAdapter
    private val llmanager = LinearLayoutManager(this)
    // Lista mutable de librería
    private val inventoryMaterialMutableList = mutableListOf<InventoryMaterial>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInventoryMaterialAdminBinding.inflate(layoutInflater)
        binding1 = ActivityInventoryMaterialAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding1.btnAddInventoryMaterial.setOnClickListener {
            // Cambiar a la vista que muestra los productos agregados
            setContentView(binding.root)
        }

        binding1.BackAdminEventCultButton.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        binding.btnSaveInventoryMaterial.setOnClickListener {
            createInventoryMaterial()
        }
        initRecyclerView()

        binding.BackAdminEventCultButton.setOnClickListener {
            setContentView(binding1.root)
        }

        // Agregar la lista al adaptador
        adapter.submitList(inventoryMaterialMutableList)
    }

    private fun createInventoryMaterial() {
        val name = binding.etName .text.toString()
        val quantityStr = binding.etQuantity.text.toString()
        val type = binding.etPrice.text.toString()

        if (name.isNotEmpty() && quantityStr.isNotEmpty() && type.isNotEmpty()) {
            val quantity = quantityStr.toInt()

            val inventoryMaterial = InventoryMaterial(
                name = name,
                quantity = quantity,
                type = type,
            )

            // Agregar el nuevo libro a la lista
            inventoryMaterialMutableList.add(inventoryMaterial)
            // Actualizar el RecyclerView con la nueva lista
            adapter.submitList(inventoryMaterialMutableList)

            // Limpiar los EditText después de agregar el libro
            binding.etName.text.clear()
            binding.etQuantity.text.clear()
            binding.etPrice.text.clear()
        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView(){
        adapter = InventoryMaterialAdapter(
            onClickListener = { inventoryMaterial -> onItemSelected(inventoryMaterial) },
            onClickDelete = { position -> onDeletedItem(position) }
        )
        binding1.recyclerInventoryMaterial.layoutManager = llmanager
        binding1.recyclerInventoryMaterial.adapter = adapter
    }

    private fun onItemSelected(inventoryMaterial: InventoryMaterial) {
        Toast.makeText(this, inventoryMaterial.name, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position: Int) {
        if (position in 0 until inventoryMaterialMutableList.size) {
            inventoryMaterialMutableList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }

}

