package com.iglesiabfr.iglesiabfrnaranjo.forums.followup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.Followup
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import org.mongodb.kbson.ObjectId


class followupForumAnswerFragment : Fragment() {
    var publicationId: ObjectId? = null
    var publicationContent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseConnector.connect()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainPublicationContent = publicationContent
        val mainPublication = view.findViewById<TextView>(R.id.mainPublication)
        mainPublication.text = mainPublicationContent
        mainPublication.textSize = 20f
        val publicationText = view.findViewById<TextInputEditText>(R.id.followupAnswerPublicationText)
        val publicationContent = publicationText.text
        val btnPublicar = view.findViewById<Button>(R.id.publishFollowupAnswerBtn)
        btnPublicar.setOnClickListener(){
            addPublication(publicationContent.toString())
        }


    }

    private fun addPublication(answerContentText: String) {
        val publicationId = publicationId
        try {
            DatabaseConnector.db.writeBlocking {
                val publication = query<Followup>("_id == $0", publicationId).find().first()
                publication.answerContent = answerContentText
                publication.answerDate = RealmInstant.now()
            }

            requireContext().toast("Se ha añadido la respuesta a la publicación")
            requireActivity().onBackPressed()
        } catch (e: Exception) {
            println("Error: $e")
            requireContext().toast("Ha habido un problema al añadir la respuesta a la publicación")
        }
    }

    private fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followup_forum_answer, container, false)
    }


}