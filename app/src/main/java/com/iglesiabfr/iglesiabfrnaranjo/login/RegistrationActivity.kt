package com.iglesiabfr.iglesiabfrnaranjo.login

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dmovil.proyecto.DatePickerFragment
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneOffset

class RegistrationActivity : AppCompatActivity() {

    private val app : App = App.create("iglesiabfr-pigqi")
    private lateinit var birthdate : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val datePicker: ImageButton = findViewById(R.id.calendar)
        datePicker.isEnabled = true
        datePicker.setOnClickListener {
            showDatePickerDialog()
        }

        val registerBtn: Button = findViewById(R.id.registerBtn)
        registerBtn.isEnabled = false
        registerBtn.setOnClickListener {
            checkInputs()
        }
    }

     // Llamada de ventanas
    private fun callLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val currentDate = LocalDate.now()
            val dateInput = LocalDate.of(year, month + 1, day)
            val age = Period.between(dateInput, currentDate).years

            // Verificar si la edad es mayor o igual a 10
            if (age < 10) {
                val dialog = AlertDialog.Builder(this@RegistrationActivity)
                    .setMessage("La edad mínima son 10 años.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                dialog.show()
                return@OnDateSetListener
            } else {
                // +1 porque enero es 0
                val formattedMonth = String.format("%02d", month + 1) // Asegura que el mes tenga dos dígitos
                val formattedDay = String.format("%02d", day) // Asegura que el día tenga dos dígitos
                val selectedDate = "$year-$formattedMonth-$formattedDay" + "T00:00:00"
                birthdate = selectedDate
            }
        })
        newFragment.show(supportFragmentManager, "datePicker")
        val registerBtn: Button = findViewById(R.id.registerBtn)
        registerBtn.isEnabled = true
    }

    // Verificar que la informacion esta completa
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkInputs() {
        val nameInput: EditText = findViewById(R.id.inputName)
        val emailInput: EditText = findViewById(R.id.inputEmail)
        val passwordInput1: EditText = findViewById(R.id.inputPassword1)
        val passwordInput2: EditText = findViewById(R.id.inputPassword2)

        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password1 = passwordInput1.text.toString()
        val password2 = passwordInput2.text.toString()

        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$".toRegex() // Debe cumplir con los requisitos de mongo

        if (name.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            val dialog = AlertDialog.Builder(this@RegistrationActivity)
                .setMessage("Todos los campos son obligatorios.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
            dialog.show()
            return
        }

        if (!passwordPattern.matches(password1)) {
            val dialog = AlertDialog.Builder(this@RegistrationActivity)
                .setMessage("La contraseña debe tener al menos 8 caracteres y contener al menos una letra y un número")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
            dialog.show()
            return
        }

        if (password1 != password2) {
            val dialog = AlertDialog.Builder(this@RegistrationActivity)
                .setMessage("Ambas contraseñas deben ser iguales.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
            dialog.show()
            return
        }

        registerUser(name)
    }

    // Crea un user que consiste de un email y contraseña
    private fun registerUser(name: String) {
        val emailInput: EditText = findViewById(R.id.inputEmail)
        val passwordInput: EditText = findViewById(R.id.inputPassword1)
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (::birthdate.isInitialized) {
            lifecycleScope.launch {
                try {
                    app.emailPasswordAuth.registerUser(email, password)
                    registerUserData(email, name)
                    Toast.makeText(this@RegistrationActivity, "Usuario registrado con éxito.", Toast.LENGTH_SHORT).show()
                    callLogin()
                } catch (e: UserAlreadyExistsException) {
                    val dialog = AlertDialog.Builder(this@RegistrationActivity)
                        .setMessage("Este usuario ya se encuentra registrado.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                    dialog.show()
                }
            }
        } else {
            val dialog = AlertDialog.Builder(this@RegistrationActivity)
                .setMessage("Debe ingresar una fecha de nacimiento.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
            dialog.show()
        }

    }

    // Agregar un documento de informacion de usuario a la coleccion UserData
    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerUserData(emailInput: String, nameInput: String) {
        val localDP = LocalDateTime.parse(birthdate)
        val realmDP = RealmInstant.from(localDP.toEpochSecond(ZoneOffset.UTC), localDP.nano)

        val event = UserData().apply {
            name = nameInput
            email = emailInput
            birthdate = realmDP
            isAdmin = false
        }
        DatabaseConnector.db.writeBlocking {
            copyToRealm(event)
        }
    }
}