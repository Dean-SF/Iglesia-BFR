package com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage

class PrincipalAdminInventoryMaterial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_inventory_material)

        val createEventCultBut : Button = findViewById(R.id.createEventBut)

        createEventCultBut.setOnClickListener {
            startActivity(Intent(this, AdminInventoryMaterial::class.java))
        }

        val backBtn: Button = findViewById(R.id.backAdminEventButton)
        backBtn.setOnClickListener {
            startActivity(Intent(this, Homepage::class.java))
        }
    }
}