package com.iglesiabfr.iglesiabfrnaranjo.admin.adminSchoolMaterial

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivityAddSchoolMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.databinding.ActivitySchoolMaterialAdminBinding
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.picker.CustomDatePicker
import com.iglesiabfr.iglesiabfrnaranjo.schema.SchoolMaterial
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

class AdminSchoolMaterial : AppCompatActivity() {
    private lateinit var realm : Realm
    private lateinit var date : LocalDate
    private lateinit var binding: ActivityAddSchoolMaterialAdminBinding
    private lateinit var binding1: ActivitySchoolMaterialAdminBinding
    private lateinit var adapter: SchoolMaterialAdapter
    private val llmanager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSchoolMaterialAdminBinding.inflate(layoutInflater)
        binding1 = ActivitySchoolMaterialAdminBinding.inflate(layoutInflater)

        val calendarBut : ImageButton = binding.dateBut
        val customDatePicker = CustomDatePicker(true)
        val datetext : TextView = binding.fechaStartMaterialSchoolinput
        val datetext1 : TextView = binding.fechaFinalMaterialSchoolinput

        realm = DatabaseConnector.db

        customDatePicker.setOnPickListener { pickedDate, dateString ->
            date = pickedDate
            datetext.text = dateString
            datetext.error = null
        }

        customDatePicker.setOnPickListener { pickedDate, dateString ->
            date = pickedDate
            datetext1.text = dateString
            datetext1.error = null
        }

        calendarBut.setOnClickListener {
            customDatePicker.show(supportFragmentManager, "tag")
        }

        datetext.setOnTouchListener { _, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_DOWN -> {
                    customDatePicker.show(supportFragmentManager, "tag")
                }
                else ->{}
            }
            true
        }

        datetext1.setOnTouchListener { _, event ->
            val action = event.action
            when(action){
                MotionEvent.ACTION_DOWN -> {
                    customDatePicker.show(supportFragmentManager, "tag")
                }
                else ->{}
            }
            true
        }

        setContentView(binding.root)

        binding1.btnAddSchoolMaterial.setOnClickListener {
            // Set content view to binding1 after adding inventory schoolMaterial
            setContentView(binding.root)
        }

        binding1.BackSchoolMaterialButton.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        binding.btnAddSchoolMaterial.setOnClickListener {
            createSchoolMaterial()
        }

        binding.BackAddSchoolMaterialButton.setOnClickListener {
            setContentView(binding1.root)
            loadSchoolMaterial() // Asegúrate de cargar los profesores al volver
        }

        initRecyclerView()
        loadSchoolMaterial() // Cargar los videos al inicio
    }

    private fun createSchoolMaterial() {
        var teacherName = binding.etTeacherName.text.toString()
        var clase = binding.etClase.text.toString()
        var initialMonth = binding.fechaStartMaterialSchoolinput.text.toString()
        var finalMonth = binding.fechaFinalMaterialSchoolinput.text.toString()

        if (teacherName.isNotEmpty() && clase.isNotEmpty() && initialMonth.isNotEmpty() && finalMonth.isNotEmpty()) {
            val schoolMaterial = SchoolMaterial().apply {
                teacherName = teacherName
                clase = clase
                initialMonth = initialMonth
                finalMonth = finalMonth
            }

            // Luego de crear el objeto Video, lo guardamos en la base de datos
            saveSchoolMaterialToDatabase(schoolMaterial)

            // Limpiar los EditText después de agregar el libro
            binding.etTeacherName.text.clear()
            binding.etClase.text.clear()
            binding.fechaStartMaterialSchoolinput.text.clear()
            binding.fechaFinalMaterialSchoolinput.text.clear()

        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSchoolMaterialToDatabase(schoolMaterial: SchoolMaterial) {
        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    copyToRealm(schoolMaterial)
                }
            }.onSuccess {
                loadSchoolMaterial() // Recargar videos después de agregar
                Toast.makeText(this@AdminSchoolMaterial, "Video guardado correctamente", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@AdminSchoolMaterial, "Error al guardar el video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteSchoolMaterialFromDatabase(schoolMaterialId: String) {
        lifecycleScope.launch {
            runCatching {
                DatabaseConnector.db.write {
                    val schoolMaterial = this.query<SchoolMaterial>("_id == $0", ObjectId(schoolMaterialId)
                    ).first().find()
                    if (schoolMaterial != null) {
                        delete(schoolMaterial)
                    }
                }
            }.onSuccess {
                loadSchoolMaterial() // Recargar videos después de eliminar
                Toast.makeText(this@AdminSchoolMaterial, "Video eliminado correctamente", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@AdminSchoolMaterial, "Error al eliminar el video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSchoolMaterial() {
        lifecycleScope.launch(Dispatchers.IO) {
            val schoolMaterials = realm.query<SchoolMaterial>().find()
            withContext(Dispatchers.Main) {
                adapter.submitList(schoolMaterials)
            }
        }
    }

    private fun initRecyclerView(){
        adapter = SchoolMaterialAdapter(
            onClickListener = { schoolMaterial: SchoolMaterial -> onItemSelected(schoolMaterial) },
            onClickDelete = { position: Int -> onDeletedItem(position) }
        )
        binding1.recyclerSchoolMaterial.layoutManager = llmanager
        binding1.recyclerSchoolMaterial.adapter = adapter
    }

    private fun onItemSelected(schoolMaterial: SchoolMaterial) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(schoolMaterial.teacherName))
        startActivity(intent)
    }

    private fun onDeletedItem(position: Int) {
        val schoolMaterial = adapter.currentList[position]
        deleteSchoolMaterialFromDatabase(schoolMaterial._id.toString())
    }
}