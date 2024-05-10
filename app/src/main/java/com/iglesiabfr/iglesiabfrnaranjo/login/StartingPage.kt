package com.iglesiabfr.iglesiabfrnaranjo.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.iglesiabfr.iglesiabfrnaranjo.R

class StartingPage : AppCompatActivity(){
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText( this,getString(R.string.NotifGranted), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,getString(R.string.NotifNotGranted), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting_page)

        askNotificationPermission()

        val loginBtn: Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            callLogin()
        }

        val registrationbtn: Button = findViewById(R.id.registerBtn)
        registrationbtn.setOnClickListener {
            callRegistration()
        }
    }

    // Llamada de ventanas
    private fun callLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun callRegistration(){
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}