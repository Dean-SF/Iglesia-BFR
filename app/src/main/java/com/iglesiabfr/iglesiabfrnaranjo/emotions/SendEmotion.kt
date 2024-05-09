package com.iglesiabfr.iglesiabfrnaranjo.emotions

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.ConfirmDialog
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.schema.Emotion
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch

class SendEmotion: AppCompatActivity() {

    private lateinit var selectedEmoImageView: ImageView
    private var user : User? = null
    private lateinit var email: String
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var confirmDialog : ConfirmDialog
    private var emotionName: String = "Feliz"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_emotion)

        loadingDialog = LoadingDialog(this)
        confirmDialog = ConfirmDialog(this)
        DatabaseConnector.connect()
        user = DatabaseConnector.getLogCurrent()
        email = DatabaseConnector.getCurrentEmail()

        val felizImg = findViewById<ImageView>(R.id.emo1)
        val enojadoImg = findViewById<ImageView>(R.id.emo2)
        val tristeImg = findViewById<ImageView>(R.id.emo3)
        val bendecidoImg = findViewById<ImageView>(R.id.emo4)
        val agradecidoImg = findViewById<ImageView>(R.id.emo5)
        val pocafeImg = findViewById<ImageView>(R.id.emo6)
        val angustiadoImg = findViewById<ImageView>(R.id.emo7)

        felizImg.setOnClickListener { selectEmotion(getString(R.string.feliz), felizImg) }
        enojadoImg.setOnClickListener { selectEmotion(getString(R.string.enojado), enojadoImg) }
        tristeImg.setOnClickListener { selectEmotion(getString(R.string.triste), tristeImg) }
        bendecidoImg.setOnClickListener { selectEmotion(getString(R.string.bendecido), bendecidoImg) }
        agradecidoImg.setOnClickListener { selectEmotion(getString(R.string.agradecido), agradecidoImg) }
        pocafeImg.setOnClickListener { selectEmotion(getString(R.string.conpocafe), pocafeImg) }
        angustiadoImg.setOnClickListener { selectEmotion(getString(R.string.angustiado), angustiadoImg) }

        selectedEmoImageView = felizImg
        selectEmotion(getString(R.string.feliz), felizImg)

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        registerBtn.setOnClickListener {
            confirmDialog.confirmation(getString(R.string.confirmEmocion))
                .setOnConfirmationListener {
                    registerEmotion() }
        }
    }

    private fun selectEmotion(emotion: String, imageView: ImageView) {
        selectedEmoImageView.background = null                // Restablecer el fondo del seleccionado anteriormente
        imageView.setBackgroundResource(R.color.lightGreen)   // Actualizar el color del seleccionado
        selectedEmoImageView = imageView
        emotionName = emotion
    }

    private fun registerEmotion() {
        loadingDialog.startLoading()
        lifecycleScope.launch {
            val userQuery = user?.let { DatabaseConnector.db.query<UserData>("email == $0", email).find().firstOrNull() }

            if (user == null || userQuery == null) {
                loadingDialog.stopLoading()
                Toast.makeText(
                    this@SendEmotion,
                    R.string.errorEmocion,
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            } else {
                val event = Emotion().apply {
                    name = userQuery.name
                    dateRegistered = RealmInstant.now()
                    emotion = emotionName
                }
                DatabaseConnector.db.writeBlocking {
                    copyToRealm(event)
                }
                loadingDialog.stopLoading()
                Toast.makeText(
                    this@SendEmotion,
                    R.string.exitoEmocion,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}