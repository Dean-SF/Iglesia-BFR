package com.iglesiabfr.iglesiabfrnaranjo.login

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dmovil.proyecto.DatePickerFragment
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

class RegistrationActivity : AppCompatActivity() {

    private val app : App = App.create("iglesiabfr-pigqi")
    private lateinit var realm : Realm
    private var user : User? = null
    private lateinit var birthdate : String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val registerBtn: Button = findViewById(R.id.registerBtn)
        registerBtn.isEnabled = false
        registerBtn.setOnClickListener {
            checkInputs()
        }
    }

    // Llamada de ventanas
//    private fun callLogin(){
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 porque enero es 0
            val lastTwoDigitsOfYear = year % 100
            val selectedDate = "$day/${month + 1}/$lastTwoDigitsOfYear"
            birthdate = selectedDate
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

        if (name.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (password1 != password2) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        registerUser()
        registerUserData(email, name, birthdate)
    }

    // Crea un user que consiste de un email y contraseña
    private fun registerUser() {
        val emailInput: EditText = findViewById(R.id.inputEmail)
        val passwordInput: EditText = findViewById(R.id.inputPassword1)
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        lifecycleScope.launch {
            app.emailPasswordAuth.registerUser(email, password)
        }
    }

    // Agregar un documento de informacion de usuario a la coleccion UserData
    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerUserData(emailInput: String, nameInput: String, birthdateInput: String) {
        val localDP = LocalDateTime.parse(birthdate)
        val realmDP = RealmInstant.from(localDP.toEpochSecond(ZoneOffset.UTC), localDP.nano)

        val event = UserData().apply {
            name = nameInput
            email = emailInput
            birthdate = realmDP
            isAdmin = false
        }
        realm.writeBlocking {
            copyToRealm(event)
        }
    }
}