package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivitySchoolMaterialAdminBinding

class AdminSchoolMaterial : AppCompatActivity() {

    private lateinit var binding: ActivitySchoolMaterialAdminBinding
    private var schoolMaterialMutableList: MutableList<SchoolMaterial> =
        SchoolMaterialProvider.schoolMaterialList.toMutableList()
    private lateinit var adapter: SchoolMaterialAdapter
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchoolMaterialAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddSchoolMaterial.setOnClickListener { createSchoolMaterial() }
        initRecyclerView()
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

            schoolMaterialMutableList.add(index = 1, schoolMaterial)
            adapter.notifyItemInserted(1)
            llmanager.scrollToPositionWithOffset(1, 10)

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
            schoolMaterialList = schoolMaterialMutableList,
            onClickListener = { schoolMaterial -> onItemSelected(schoolMaterial) },
            onClickDelete = { position -> onDeletedItem(position) }
        )
        binding.recyclerSchoolMaterial.layoutManager = llmanager
        binding.recyclerSchoolMaterial.adapter = adapter
    }

    private fun onItemSelected(schoolMaterial: SchoolMaterial) {
        Toast.makeText(this, schoolMaterial.clase, Toast.LENGTH_SHORT).show()
    }

    private fun onDeletedItem(position: Int) {
        schoolMaterialMutableList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

}