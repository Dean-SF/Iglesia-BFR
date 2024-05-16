package com.iglesiabfr.iglesiabfrnaranjo.admin.adminSchoolMaterial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddSchoolMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivitySchoolMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage

class AdminSchoolMaterial : AppCompatActivity() {

    private lateinit var binding: ActivityAddSchoolMaterialAdminBinding
    private lateinit var binding1: ActivitySchoolMaterialAdminBinding
    private lateinit var adapter: SchoolMaterialAdapter
    private val llmanager = LinearLayoutManager(this)
    private val schoolMaterialMutableList = mutableListOf<SchoolMaterial>() // Lista mutable de la materia de la escuela

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSchoolMaterialAdminBinding.inflate(layoutInflater)
        binding1 = ActivitySchoolMaterialAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding1.btnAddSchoolMaterial.setOnClickListener {
            // Set content view to binding1 after adding inventory schoolMaterial
            setContentView(binding.root)
        }

        binding1.BackAdminEventCultButton.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        binding.btnAddSchoolMaterial.setOnClickListener {
            createSchoolMaterial()
        }
        initRecyclerView()

        binding.BackAdminEventCultButton.setOnClickListener {
            setContentView(binding1.root)
        }
    }

    private fun createSchoolMaterial() {
        val teacherName = binding.etTeacherName.text.toString()
        val clase = binding.etClase.text.toString()
        val initialMonth = binding.etInitialMonth.text.toString()
        val finalMonth = binding.etFinalMonth.text.toString()

        if (teacherName.isNotEmpty() && clase.isNotEmpty() && initialMonth.isNotEmpty() && finalMonth.isNotEmpty()) {
            val schoolMaterial = SchoolMaterial(
                teacherName = teacherName,
                clase = clase,
                initialMonth = initialMonth,
                finalMonth = finalMonth
            )

            schoolMaterialMutableList.add(schoolMaterial)
            adapter.submitList(schoolMaterialMutableList)

            // Limpiar los EditText después de agregar el libro
            binding.etTeacherName.text.clear()
            binding.etClase.text.clear()
            binding.etInitialMonth.text.clear()
            binding.etFinalMonth.text.clear()
        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView(){
        adapter = SchoolMaterialAdapter(
            onClickListener = { schoolMaterial -> onItemSelected(schoolMaterial) },
            onClickDelete = { position -> onDeletedItem(position) }
        )
        binding1.recyclerSchoolMaterial.layoutManager = llmanager
        binding1.recyclerSchoolMaterial.adapter = adapter
    }

    private fun onItemSelected(schoolMaterial: SchoolMaterial) {
        Toast.makeText(this, schoolMaterial.clase, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position: Int) {
        if (position in 0 until schoolMaterialMutableList.size) {
            schoolMaterialMutableList.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }

}