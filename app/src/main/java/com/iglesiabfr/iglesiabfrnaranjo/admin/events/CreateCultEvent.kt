package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import io.realm.kotlin.Realm
import io.realm.kotlin.types.RealmInstant
import java.time.LocalDate

class CreateCultEvent : AppCompatActivity() {
    private lateinit var realm: Realm
    private lateinit var date : LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cult)

        // Inicializar la fecha con la fecha actual
        date = LocalDate.now()

        // Obtener referencias de los elementos de la interfaz de usuario
        val nameInput: EditText = findViewById(R.id.nameInputCult)
        val descInput: EditText = findViewById(R.id.descInput)
        val createEventBut: Button = findViewById(R.id.createEventBut)
        val calendarBut: ImageButton = findViewById(R.id.dateBut)

        // Listener para el botón "Crear Evento"
        createEventBut.setOnClickListener {
            val name: String = nameInput.text.toString()
            val desc: String = descInput.text.toString()

            // Crear un objeto Event y asignar los valores
            val event = Event().apply {
                this.name = name
                // Convertir la fecha a un RealmInstant
                this.date = RealmInstant.now()
                this.desc = desc
            }

            // Guardar el objeto Event en la base de datos Realm
            realm.writeBlocking {
                copyToRealm(event)
            }
        }

        // Listener para el botón del calendario
        calendarBut.setOnClickListener {
            showDatePickerDialog()
        }

        val backBtn: Button = findViewById(R.id.BackEventBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, AdminCult::class.java)
            startActivity(intent)
        }
    }

    // Método para mostrar el diálogo de selección de fecha
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                date = LocalDate.of(year, month + 1, dayOfMonth) // El mes comienza en 0
                updateDate()
            },
            date.year, // Año actual
            date.monthValue - 1, // Mes actual (comienza en 0)
            date.dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun updateDate() {
        val datetext : TextView = findViewById(R.id.fechaInput)
        datetext.text = date.toString()
    }
}