package com.iglesiabfr.iglesiabfrnaranjo.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        val loginBtn: Button = findViewById(R.id.loginBtn)
//        loginBtn.setOnClickListener {
//            this.login()
//        }
    }

    // Llamada de ventanas
    private fun callMainMenu(){
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    // Validar datos del usuario y hacer login
//    private fun login() {
//        TO-DO
//    }
}