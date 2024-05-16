package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage

class PrincipalAdminSchoolMaterial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_school_material)

        val createEventCultBut : Button = findViewById(R.id.createEventBut)

        createEventCultBut.setOnClickListener {
            val i = Intent(this, AdminSchoolMaterial::class.java)
            startActivity(i)
        }

        val backBtn: Button = findViewById(R.id.backAdminEventButton)
        backBtn.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }
    }
}